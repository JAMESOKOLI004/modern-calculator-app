package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.CalculationDao
import com.example.data.CalculatorDatabase
import com.example.data.CalculationEntity
import com.example.data.CalculatorSettings
import com.example.data.SettingsManager
import com.example.data.ThemeMode
import com.example.domain.CalculatorEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.math.BigDecimal

data class CalculatorUiState(
    val expression: String = "",
    val result: String = "0",
    val previewResult: String? = null,
    val isEvaluated: Boolean = false,
    val errorMessage: String? = null
)

class CalculatorViewModel(application: Application) : AndroidViewModel(application) {

    private val db = CalculatorDatabase.getDatabase(application)
    private val dao: CalculationDao = db.calculationDao()
    private val settingsManager = SettingsManager(application)

    val settings: StateFlow<CalculatorSettings> = settingsManager.settings

    val history: StateFlow<List<CalculationEntity>> = dao.getAllCalculations()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _uiState = MutableStateFlow(CalculatorUiState())
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    fun onDigit(digit: Char) {
        val current = _uiState.value
        val precision = settings.value.decimalPrecision
        val separator = settings.value.thousandsSeparator

        if (current.isEvaluated) {
            // Start fresh if user taps digit after equals
            val newExpr = digit.toString()
            _uiState.value = CalculatorUiState(
                expression = newExpr,
                result = newExpr,
                previewResult = null,
                isEvaluated = false
            )
            return
        }

        val newExpr = if (current.expression.isEmpty() || current.expression == "0") {
            digit.toString()
        } else {
            current.expression + digit
        }

        val preview = CalculatorEngine.evaluatePreview(newExpr, precision, separator)
        _uiState.value = current.copy(
            expression = newExpr,
            result = getCurrentToken(newExpr),
            previewResult = preview,
            errorMessage = null
        )
    }

    fun onDecimal() {
        val current = _uiState.value
        val precision = settings.value.decimalPrecision
        val separator = settings.value.thousandsSeparator

        if (current.isEvaluated) {
            _uiState.value = CalculatorUiState(
                expression = "0.",
                result = "0.",
                isEvaluated = false
            )
            return
        }

        val expr = current.expression
        val currentToken = getCurrentToken(expr)

        if (currentToken.contains(".")) {
            // Decimal already exists in the current number token
            return
        }

        val newExpr = when {
            expr.isEmpty() -> "0."
            expr.endsWith(" ") -> expr + "0."
            else -> expr + "."
        }

        val preview = CalculatorEngine.evaluatePreview(newExpr, precision, separator)
        _uiState.value = current.copy(
            expression = newExpr,
            result = getCurrentToken(newExpr),
            previewResult = preview
        )
    }

    fun onOperator(op: String) {
        val current = _uiState.value
        val precision = settings.value.decimalPrecision
        val separator = settings.value.thousandsSeparator

        var baseExpr = current.expression.trim()

        if (current.isEvaluated) {
            // Continue calculating using the evaluated result
            baseExpr = current.result.replace(",", "")
        }

        if (baseExpr.isEmpty()) {
            if (op == "-") {
                // Negative prefix
                _uiState.value = CalculatorUiState(
                    expression = "-",
                    result = "-",
                    isEvaluated = false
                )
                return
            }
            baseExpr = "0"
        }

        val tokens = baseExpr.split(" ").filter { it.isNotEmpty() }
        val lastToken = tokens.lastOrNull() ?: ""

        val newExpr = if (CalculatorEngine.OPERATORS.contains(lastToken)) {
            // Replace last operator
            val allExceptLast = tokens.dropLast(1).joinToString(" ")
            if (allExceptLast.isEmpty()) "$op " else "$allExceptLast $op "
        } else {
            "$baseExpr $op "
        }

        val preview = CalculatorEngine.evaluatePreview(newExpr, precision, separator)
        _uiState.value = CalculatorUiState(
            expression = newExpr,
            result = current.result,
            previewResult = preview,
            isEvaluated = false
        )
    }

    fun onToggleSign() {
        val current = _uiState.value
        val precision = settings.value.decimalPrecision
        val separator = settings.value.thousandsSeparator

        if (current.isEvaluated) {
            val num = current.result.replace(",", "")
            val inverted = if (num.startsWith("-")) num.substring(1) else "-$num"
            _uiState.value = CalculatorUiState(
                expression = inverted,
                result = inverted,
                isEvaluated = false
            )
            return
        }

        val expr = current.expression.trim()
        if (expr.isEmpty()) {
            _uiState.value = current.copy(expression = "-", result = "-")
            return
        }

        val tokens = expr.split(" ").filter { it.isNotEmpty() }.toMutableList()
        val lastIndex = tokens.lastIndex
        val lastToken = tokens[lastIndex]

        if (!CalculatorEngine.OPERATORS.contains(lastToken)) {
            // Toggle sign of last number
            val toggled = if (lastToken.startsWith("-")) {
                lastToken.substring(1)
            } else {
                "-$lastToken"
            }
            tokens[lastIndex] = toggled
            val newExpr = tokens.joinToString(" ")
            val preview = CalculatorEngine.evaluatePreview(newExpr, precision, separator)
            _uiState.value = current.copy(
                expression = newExpr,
                result = toggled,
                previewResult = preview
            )
        }
    }

    fun onPercentage() {
        val current = _uiState.value
        val precision = settings.value.decimalPrecision
        val separator = settings.value.thousandsSeparator

        val expr = current.expression.trim()
        if (expr.isEmpty()) return

        val newExpr = if (!expr.endsWith("%") && !CalculatorEngine.OPERATORS.contains(expr.takeLast(1))) {
            "$expr%"
        } else {
            expr
        }

        when (val eval = CalculatorEngine.evaluate(newExpr, precision, separator)) {
            is CalculatorEngine.EvaluationResult.Success -> {
                _uiState.value = current.copy(
                    expression = newExpr,
                    result = eval.formatted,
                    previewResult = null
                )
            }
            is CalculatorEngine.EvaluationResult.Error -> {
                // Ignore invalid percent
            }
        }
    }

    fun onClear() {
        _uiState.value = CalculatorUiState(
            expression = "",
            result = "0",
            previewResult = null,
            isEvaluated = false,
            errorMessage = null
        )
    }

    fun onBackspace() {
        val current = _uiState.value
        val precision = settings.value.decimalPrecision
        val separator = settings.value.thousandsSeparator

        if (current.isEvaluated) {
            onClear()
            return
        }

        val expr = current.expression
        if (expr.isEmpty()) return

        val newExpr = if (expr.endsWith(" ")) {
            expr.dropLast(2) // Drop operator and space
        } else {
            expr.dropLast(1)
        }

        val preview = CalculatorEngine.evaluatePreview(newExpr, precision, separator)
        val token = getCurrentToken(newExpr)

        _uiState.value = current.copy(
            expression = newExpr,
            result = if (token.isEmpty()) "0" else token,
            previewResult = preview
        )
    }

    fun onEquals() {
        val current = _uiState.value
        val precision = settings.value.decimalPrecision
        val separator = settings.value.thousandsSeparator
        val expr = current.expression.trim()

        if (expr.isEmpty()) return

        when (val eval = CalculatorEngine.evaluate(expr, precision, separator)) {
            is CalculatorEngine.EvaluationResult.Success -> {
                val formattedResult = eval.formatted
                _uiState.value = CalculatorUiState(
                    expression = expr,
                    result = formattedResult,
                    previewResult = null,
                    isEvaluated = true
                )

                // Save to history in Room
                viewModelScope.launch {
                    dao.insert(
                        CalculationEntity(
                            expression = expr,
                            result = formattedResult
                        )
                    )
                }
            }
            is CalculatorEngine.EvaluationResult.Error -> {
                _uiState.value = current.copy(
                    result = eval.message,
                    previewResult = null,
                    errorMessage = eval.message,
                    isEvaluated = true
                )
            }
        }
    }

    fun onReuseExpression(expr: String) {
        val precision = settings.value.decimalPrecision
        val separator = settings.value.thousandsSeparator
        val preview = CalculatorEngine.evaluatePreview(expr, precision, separator)
        _uiState.value = CalculatorUiState(
            expression = expr,
            result = getCurrentToken(expr),
            previewResult = preview,
            isEvaluated = false
        )
    }

    fun onUseResult(result: String) {
        val cleanResult = result.replace(",", "")
        _uiState.value = CalculatorUiState(
            expression = cleanResult,
            result = result,
            previewResult = null,
            isEvaluated = false
        )
    }

    fun onClearHistory() {
        viewModelScope.launch {
            dao.clearAll()
        }
    }

    fun onDeleteHistoryItem(item: CalculationEntity) {
        viewModelScope.launch {
            dao.delete(item)
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        settingsManager.setThemeMode(mode)
    }

    fun setHapticFeedback(enabled: Boolean) {
        settingsManager.setHapticFeedback(enabled)
    }

    fun setDecimalPrecision(precision: Int) {
        settingsManager.setDecimalPrecision(precision)
        // Refresh display result with new precision
        refreshCurrentResult(precision, settings.value.thousandsSeparator)
    }

    fun setThousandsSeparator(enabled: Boolean) {
        settingsManager.setThousandsSeparator(enabled)
        refreshCurrentResult(settings.value.decimalPrecision, enabled)
    }

    private fun refreshCurrentResult(precision: Int, separator: Boolean) {
        val current = _uiState.value
        if (current.expression.isNotEmpty()) {
            when (val eval = CalculatorEngine.evaluate(current.expression, precision, separator)) {
                is CalculatorEngine.EvaluationResult.Success -> {
                    if (current.isEvaluated) {
                        _uiState.value = current.copy(result = eval.formatted)
                    } else {
                        _uiState.value = current.copy(previewResult = eval.formatted)
                    }
                }
                else -> Unit
            }
        }
    }

    private fun getCurrentToken(expr: String): String {
        val trimmed = expr.trim()
        if (trimmed.isEmpty()) return "0"
        val tokens = trimmed.split(" ").filter { it.isNotEmpty() }
        val last = tokens.lastOrNull() ?: "0"
        return if (CalculatorEngine.OPERATORS.contains(last)) {
            tokens.getOrNull(tokens.size - 2) ?: "0"
        } else {
            last
        }
    }
}
