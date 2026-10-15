package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ui.components.ButtonCategory
import com.example.ui.components.CalculatorButton
import com.example.ui.components.DisplayArea
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val history by viewModel.history.collectAsState()
    val settings by viewModel.settings.collectAsState()

    val historySheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val settingsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var showHistorySheet by remember { mutableStateOf(false) }
    var showSettingsSheet by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 480.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top: Large right-aligned display area with swipe-down gesture to reveal history
                DisplayArea(
                    expression = uiState.expression,
                    result = uiState.result,
                    previewResult = uiState.previewResult,
                    historyCount = history.size,
                    isDarkTheme = isDarkTheme,
                    onSwipeDown = {
                        showHistorySheet = true
                    },
                    onOpenHistory = {
                        showHistorySheet = true
                    },
                    onOpenSettings = {
                        showSettingsSheet = true
                    },
                    onBackspace = {
                        viewModel.onBackspace()
                    },
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Bottom: 4-column button grid
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Row 1: Function buttons (C, +/-, %) and Operator ÷
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CalculatorButton(
                            symbol = if (uiState.expression.isEmpty() && uiState.result == "0") "AC" else "C",
                            category = ButtonCategory.FUNCTION,
                            isDarkTheme = isDarkTheme,
                            hapticEnabled = settings.hapticFeedback,
                            modifier = Modifier.weight(1f),
                            contentDescriptionText = "Clear",
                            testTag = "btn_clear",
                            onClick = { viewModel.onClear() }
                        )
                        CalculatorButton(
                            symbol = "+/-",
                            category = ButtonCategory.FUNCTION,
                            isDarkTheme = isDarkTheme,
                            hapticEnabled = settings.hapticFeedback,
                            modifier = Modifier.weight(1f),
                            contentDescriptionText = "Toggle sign",
                            testTag = "btn_plus_minus",
                            onClick = { viewModel.onToggleSign() }
                        )
                        CalculatorButton(
                            symbol = "%",
                            category = ButtonCategory.FUNCTION,
                            isDarkTheme = isDarkTheme,
                            hapticEnabled = settings.hapticFeedback,
                            modifier = Modifier.weight(1f),
                            contentDescriptionText = "Percentage",
                            testTag = "btn_percent",
                            onClick = { viewModel.onPercentage() }
                        )
                        CalculatorButton(
                            symbol = "÷",
                            category = ButtonCategory.OPERATOR,
                            isDarkTheme = isDarkTheme,
                            hapticEnabled = settings.hapticFeedback,
                            modifier = Modifier.weight(1f),
                            contentDescriptionText = "Divide",
                            testTag = "btn_divide",
                            onClick = { viewModel.onOperator("÷") }
                        )
                    }

                    // Row 2: 7, 8, 9, ×
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CalculatorButton(
                            symbol = "7",
                            category = ButtonCategory.NUMBER,
                            isDarkTheme = isDarkTheme,
                            hapticEnabled = settings.hapticFeedback,
                            modifier = Modifier.weight(1f),
                            testTag = "btn_7",
                            onClick = { viewModel.onDigit('7') }
                        )
                        CalculatorButton(
                            symbol = "8",
                            category = ButtonCategory.NUMBER,
                            isDarkTheme = isDarkTheme,
                            hapticEnabled = settings.hapticFeedback,
                            modifier = Modifier.weight(1f),
                            testTag = "btn_8",
                            onClick = { viewModel.onDigit('8') }
                        )
                        CalculatorButton(
                            symbol = "9",
                            category = ButtonCategory.NUMBER,
                            isDarkTheme = isDarkTheme,
                            hapticEnabled = settings.hapticFeedback,
                            modifier = Modifier.weight(1f),
                            testTag = "btn_9",
                            onClick = { viewModel.onDigit('9') }
                        )
                        CalculatorButton(
                            symbol = "×",
                            category = ButtonCategory.OPERATOR,
                            isDarkTheme = isDarkTheme,
                            hapticEnabled = settings.hapticFeedback,
                            modifier = Modifier.weight(1f),
                            contentDescriptionText = "Multiply",
                            testTag = "btn_multiply",
                            onClick = { viewModel.onOperator("×") }
                        )
                    }

                    // Row 3: 4, 5, 6, -
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CalculatorButton(
                            symbol = "4",
                            category = ButtonCategory.NUMBER,
                            isDarkTheme = isDarkTheme,
                            hapticEnabled = settings.hapticFeedback,
                            modifier = Modifier.weight(1f),
                            testTag = "btn_4",
                            onClick = { viewModel.onDigit('4') }
                        )
                        CalculatorButton(
                            symbol = "5",
                            category = ButtonCategory.NUMBER,
                            isDarkTheme = isDarkTheme,
                            hapticEnabled = settings.hapticFeedback,
                            modifier = Modifier.weight(1f),
                            testTag = "btn_5",
                            onClick = { viewModel.onDigit('5') }
                        )
                        CalculatorButton(
                            symbol = "6",
                            category = ButtonCategory.NUMBER,
                            isDarkTheme = isDarkTheme,
                            hapticEnabled = settings.hapticFeedback,
                            modifier = Modifier.weight(1f),
                            testTag = "btn_6",
                            onClick = { viewModel.onDigit('6') }
                        )
                        CalculatorButton(
                            symbol = "-",
                            category = ButtonCategory.OPERATOR,
                            isDarkTheme = isDarkTheme,
                            hapticEnabled = settings.hapticFeedback,
                            modifier = Modifier.weight(1f),
                            contentDescriptionText = "Subtract",
                            testTag = "btn_subtract",
                            onClick = { viewModel.onOperator("-") }
                        )
                    }

                    // Row 4: 1, 2, 3, +
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CalculatorButton(
                            symbol = "1",
                            category = ButtonCategory.NUMBER,
                            isDarkTheme = isDarkTheme,
                            hapticEnabled = settings.hapticFeedback,
                            modifier = Modifier.weight(1f),
                            testTag = "btn_1",
                            onClick = { viewModel.onDigit('1') }
                        )
                        CalculatorButton(
                            symbol = "2",
                            category = ButtonCategory.NUMBER,
                            isDarkTheme = isDarkTheme,
                            hapticEnabled = settings.hapticFeedback,
                            modifier = Modifier.weight(1f),
                            testTag = "btn_2",
                            onClick = { viewModel.onDigit('2') }
                        )
                        CalculatorButton(
                            symbol = "3",
                            category = ButtonCategory.NUMBER,
                            isDarkTheme = isDarkTheme,
                            hapticEnabled = settings.hapticFeedback,
                            modifier = Modifier.weight(1f),
                            testTag = "btn_3",
                            onClick = { viewModel.onDigit('3') }
                        )
                        CalculatorButton(
                            symbol = "+",
                            category = ButtonCategory.OPERATOR,
                            isDarkTheme = isDarkTheme,
                            hapticEnabled = settings.hapticFeedback,
                            modifier = Modifier.weight(1f),
                            contentDescriptionText = "Add",
                            testTag = "btn_add",
                            onClick = { viewModel.onOperator("+") }
                        )
                    }

                    // Row 5: 0, ., ⌫, =
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CalculatorButton(
                            symbol = "0",
                            category = ButtonCategory.NUMBER,
                            isDarkTheme = isDarkTheme,
                            hapticEnabled = settings.hapticFeedback,
                            modifier = Modifier.weight(1f),
                            testTag = "btn_0",
                            onClick = { viewModel.onDigit('0') }
                        )
                        CalculatorButton(
                            symbol = ".",
                            category = ButtonCategory.NUMBER,
                            isDarkTheme = isDarkTheme,
                            hapticEnabled = settings.hapticFeedback,
                            modifier = Modifier.weight(1f),
                            contentDescriptionText = "Decimal point",
                            testTag = "btn_decimal",
                            onClick = { viewModel.onDecimal() }
                        )
                        CalculatorButton(
                            symbol = "⌫",
                            category = ButtonCategory.FUNCTION,
                            isDarkTheme = isDarkTheme,
                            hapticEnabled = settings.hapticFeedback,
                            modifier = Modifier.weight(1f),
                            contentDescriptionText = "Backspace",
                            testTag = "btn_backspace",
                            onClick = { viewModel.onBackspace() }
                        )
                        CalculatorButton(
                            symbol = "=",
                            category = ButtonCategory.OPERATOR,
                            isDarkTheme = isDarkTheme,
                            hapticEnabled = settings.hapticFeedback,
                            modifier = Modifier.weight(1f),
                            contentDescriptionText = "Equals",
                            testTag = "btn_equals",
                            onClick = { viewModel.onEquals() }
                        )
                    }
                }
            }
        }

        // Slide-up History Panel
        if (showHistorySheet) {
            HistoryBottomSheet(
                sheetState = historySheetState,
                historyList = history,
                isDarkTheme = isDarkTheme,
                onDismiss = {
                    coroutineScope.launch { historySheetState.hide() }.invokeOnCompletion {
                        showHistorySheet = false
                    }
                },
                onReuseExpression = { expr ->
                    viewModel.onReuseExpression(expr)
                },
                onUseResult = { res ->
                    viewModel.onUseResult(res)
                },
                onClearHistory = {
                    viewModel.onClearHistory()
                },
                onDeleteItem = { item ->
                    viewModel.onDeleteHistoryItem(item)
                }
            )
        }

        // Settings Sheet
        if (showSettingsSheet) {
            SettingsSheet(
                sheetState = settingsSheetState,
                settings = settings,
                isDarkTheme = isDarkTheme,
                onDismiss = {
                    coroutineScope.launch { settingsSheetState.hide() }.invokeOnCompletion {
                        showSettingsSheet = false
                    }
                },
                onSetThemeMode = { mode ->
                    viewModel.setThemeMode(mode)
                },
                onSetHaptic = { enabled ->
                    viewModel.setHapticFeedback(enabled)
                },
                onSetPrecision = { precision ->
                    viewModel.setDecimalPrecision(precision)
                },
                onSetThousandsSeparator = { enabled ->
                    viewModel.setThousandsSeparator(enabled)
                }
            )
        }
    }
}
