package com.example.domain

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object CalculatorEngine {

    val OPERATORS = setOf("+", "-", "×", "÷")

    sealed class EvaluationResult {
        data class Success(val value: BigDecimal, val formatted: String) : EvaluationResult()
        data class Error(val message: String) : EvaluationResult()
    }

    /**
     * Evaluates a mathematical string expression like "125 + 4 × 2 - 10".
     */
    fun evaluate(
        expression: String,
        precision: Int = -1,
        useThousandsSeparator: Boolean = true
    ): EvaluationResult {
        val clean = expression.trim()
        if (clean.isEmpty()) {
            return EvaluationResult.Success(BigDecimal.ZERO, "0")
        }

        return try {
            val tokens = tokenize(clean)
            if (tokens.isEmpty()) {
                return EvaluationResult.Success(BigDecimal.ZERO, "0")
            }

            // If last token is an operator, remove it for evaluation (e.g. "12 + " -> evaluates "12")
            val evaluableTokens = if (isOperator(tokens.last())) {
                tokens.dropLast(1)
            } else {
                tokens
            }

            if (evaluableTokens.isEmpty()) {
                return EvaluationResult.Success(BigDecimal.ZERO, "0")
            }

            val result = evaluateTokens(evaluableTokens)
            val formatted = formatNumber(result, precision, useThousandsSeparator)
            EvaluationResult.Success(result, formatted)
        } catch (e: ArithmeticException) {
            EvaluationResult.Error("Cannot divide by 0")
        } catch (e: Exception) {
            EvaluationResult.Error("Format Error")
        }
    }

    /**
     * Evaluates live preview if the expression has at least one operator and ends with a number.
     */
    fun evaluatePreview(
        expression: String,
        precision: Int = -1,
        useThousandsSeparator: Boolean = true
    ): String? {
        val clean = expression.trim()
        if (clean.isEmpty()) return null

        val tokens = tokenize(clean)
        val hasOperator = tokens.any { isOperator(it) }
        if (!hasOperator) return null
        if (tokens.isEmpty() || isOperator(tokens.last())) return null

        return when (val eval = evaluate(clean, precision, useThousandsSeparator)) {
            is EvaluationResult.Success -> eval.formatted
            is EvaluationResult.Error -> null
        }
    }

    private fun isOperator(token: String): Boolean = OPERATORS.contains(token)

    private fun tokenize(expr: String): List<String> {
        val tokens = mutableListOf<String>()
        var currentNumber = StringBuilder()

        var i = 0
        while (i < expr.length) {
            val c = expr[i]
            when {
                c.isWhitespace() -> {
                    if (currentNumber.isNotEmpty()) {
                        tokens.add(currentNumber.toString())
                        currentNumber = StringBuilder()
                    }
                }
                c == '+' || c == '×' || c == '÷' -> {
                    if (currentNumber.isNotEmpty()) {
                        tokens.add(currentNumber.toString())
                        currentNumber = StringBuilder()
                    }
                    tokens.add(c.toString())
                }
                c == '-' -> {
                    // Check if '-' is a negative sign for a number or a subtraction operator
                    val isUnary = currentNumber.isEmpty() && (tokens.isEmpty() || isOperator(tokens.last()))
                    if (isUnary) {
                        currentNumber.append(c)
                    } else {
                        if (currentNumber.isNotEmpty()) {
                            tokens.add(currentNumber.toString())
                            currentNumber = StringBuilder()
                        }
                        tokens.add("-")
                    }
                }
                c == '%' -> {
                    if (currentNumber.isNotEmpty()) {
                        tokens.add(currentNumber.toString())
                        currentNumber = StringBuilder()
                    }
                    tokens.add("%")
                }
                else -> {
                    currentNumber.append(c)
                }
            }
            i++
        }

        if (currentNumber.isNotEmpty()) {
            tokens.add(currentNumber.toString())
        }
        return tokens
    }

    private fun evaluateTokens(tokens: List<String>): BigDecimal {
        // Step 1: Process percentage tokens
        val step1 = mutableListOf<String>()
        var i = 0
        while (i < tokens.size) {
            val token = tokens[i]
            if (token == "%") {
                if (step1.isNotEmpty()) {
                    val prevVal = BigDecimal(step1.removeAt(step1.lastIndex))
                    // If preceded by an operator, percentage of preceding base value
                    // e.g. 100 + 10% -> 10% of 100 is 10
                    if (step1.size >= 2 && isOperator(step1[step1.lastIndex])) {
                        val baseVal = BigDecimal(step1[step1.lastIndex - 1])
                        val percentVal = baseVal.multiply(prevVal, MathContext.DECIMAL128)
                            .divide(BigDecimal(100), MathContext.DECIMAL128)
                        step1.add(percentVal.toPlainString())
                    } else {
                        val percentVal = prevVal.divide(BigDecimal(100), MathContext.DECIMAL128)
                        step1.add(percentVal.toPlainString())
                    }
                }
            } else {
                step1.add(token)
            }
            i++
        }

        if (step1.isEmpty()) return BigDecimal.ZERO

        // Step 2: Process multiplication and division (high precedence)
        val step2 = mutableListOf<String>()
        var j = 0
        while (j < step1.size) {
            val token = step1[j]
            if (token == "×" || token == "÷") {
                if (step2.isNotEmpty() && j + 1 < step1.size) {
                    val left = BigDecimal(step2.removeAt(step2.lastIndex))
                    val right = BigDecimal(step1[j + 1])
                    val res = if (token == "×") {
                        left.multiply(right, MathContext.DECIMAL128)
                    } else {
                        if (right.compareTo(BigDecimal.ZERO) == 0) {
                            throw ArithmeticException("Division by zero")
                        }
                        left.divide(right, MathContext.DECIMAL128)
                    }
                    step2.add(res.toPlainString())
                    j += 2
                    continue
                }
            }
            step2.add(token)
            j++
        }

        if (step2.isEmpty()) return BigDecimal.ZERO

        // Step 3: Process addition and subtraction (left to right)
        var result = BigDecimal(step2[0])
        var k = 1
        while (k < step2.size) {
            val op = step2[k]
            if (k + 1 < step2.size) {
                val nextVal = BigDecimal(step2[k + 1])
                result = when (op) {
                    "+" -> result.add(nextVal, MathContext.DECIMAL128)
                    "-" -> result.subtract(nextVal, MathContext.DECIMAL128)
                    else -> result
                }
                k += 2
            } else {
                k++
            }
        }

        return result
    }

    fun formatNumber(
        value: BigDecimal,
        precision: Int = -1,
        useThousandsSeparator: Boolean = true
    ): String {
        // Handle scientific notation for extreme numbers
        val doubleVal = value.toDouble()
        if (!doubleVal.isInfinite() && !doubleVal.isNaN()) {
            val absVal = kotlin.math.abs(doubleVal)
            if (absVal > 0 && (absVal >= 1e12 || absVal < 1e-7)) {
                return String.format(Locale.US, "%.6e", doubleVal)
            }
        }

        val symbols = DecimalFormatSymbols(Locale.US).apply {
            if (useThousandsSeparator) {
                groupingSeparator = ','
            }
            decimalSeparator = '.'
        }

        val pattern = StringBuilder(if (useThousandsSeparator) "#,##0" else "0")

        if (precision >= 0) {
            if (precision > 0) {
                pattern.append(".")
                for (idx in 0 until precision) {
                    pattern.append("0")
                }
            }
            val rounded = value.setScale(precision, RoundingMode.HALF_UP)
            val df = DecimalFormat(pattern.toString(), symbols)
            return df.format(rounded)
        } else {
            // Auto precision: trim trailing zeros up to 10 decimals
            val scaled = value.setScale(10, RoundingMode.HALF_UP).stripTrailingZeros()
            val scale = scaled.scale()
            if (scale > 0) {
                pattern.append(".")
                for (idx in 0 until scale) {
                    pattern.append("#")
                }
            }
            val df = DecimalFormat(pattern.toString(), symbols)
            return df.format(scaled)
        }
    }
}
