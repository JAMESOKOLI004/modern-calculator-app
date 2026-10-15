package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CalculatorSettings
import com.example.data.ThemeMode
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.FunctionLightGray
import com.example.ui.theme.LightFunctionBg
import com.example.ui.theme.LightSurface
import com.example.ui.theme.LightTextPrimary
import com.example.ui.theme.LightTextSecondary
import com.example.ui.theme.NumberDarkGray
import com.example.ui.theme.OrangeOperator
import com.example.ui.theme.TextMutedGray
import com.example.ui.theme.TextWhite

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SettingsSheet(
    sheetState: SheetState,
    settings: CalculatorSettings,
    isDarkTheme: Boolean,
    onDismiss: () -> Unit,
    onSetThemeMode: (ThemeMode) -> Unit,
    onSetHaptic: (Boolean) -> Unit,
    onSetPrecision: (Int) -> Unit,
    onSetThousandsSeparator: (Boolean) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = if (isDarkTheme) DarkBackground else LightSurface,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .width(44.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(if (isDarkTheme) FunctionLightGray else LightFunctionBg)
            )
        },
        modifier = Modifier.testTag("settings_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 36.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 20.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = OrangeOperator,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Calculator Settings",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkTheme) TextWhite else LightTextPrimary
                )
            }

            // Section 1: Theme Toggle
            SettingsCard(
                title = "Appearance",
                icon = Icons.Default.DarkMode,
                isDarkTheme = isDarkTheme
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ThemeOptionButton(
                        label = "Dark",
                        icon = Icons.Default.DarkMode,
                        selected = settings.themeMode == ThemeMode.DARK,
                        isDarkTheme = isDarkTheme,
                        modifier = Modifier.weight(1f),
                        onClick = { onSetThemeMode(ThemeMode.DARK) }
                    )
                    ThemeOptionButton(
                        label = "Light",
                        icon = Icons.Default.LightMode,
                        selected = settings.themeMode == ThemeMode.LIGHT,
                        isDarkTheme = isDarkTheme,
                        modifier = Modifier.weight(1f),
                        onClick = { onSetThemeMode(ThemeMode.LIGHT) }
                    )
                    ThemeOptionButton(
                        label = "System",
                        icon = Icons.Default.Smartphone,
                        selected = settings.themeMode == ThemeMode.SYSTEM,
                        isDarkTheme = isDarkTheme,
                        modifier = Modifier.weight(1f),
                        onClick = { onSetThemeMode(ThemeMode.SYSTEM) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Section 2: Haptic Feedback Toggle
            SettingsCard(
                title = "Feedback",
                icon = Icons.Default.Vibration,
                isDarkTheme = isDarkTheme
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Haptic Vibration",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isDarkTheme) TextWhite else LightTextPrimary
                        )
                        Text(
                            text = "Vibrate on button press for tactile feel",
                            fontSize = 12.sp,
                            color = if (isDarkTheme) TextMutedGray else LightTextSecondary
                        )
                    }
                    Switch(
                        checked = settings.hapticFeedback,
                        onCheckedChange = onSetHaptic,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = TextWhite,
                            checkedTrackColor = OrangeOperator
                        ),
                        modifier = Modifier.testTag("switch_haptic_feedback")
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Section 3: Decimal Precision
            SettingsCard(
                title = "Decimal Precision",
                icon = Icons.Default.FormatListNumbered,
                isDarkTheme = isDarkTheme
            ) {
                Column {
                    Text(
                        text = "Select number of decimal digits to display in results:",
                        fontSize = 12.sp,
                        color = if (isDarkTheme) TextMutedGray else LightTextSecondary,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    val precisionOptions = listOf(
                        -1 to "Auto",
                        0 to "0",
                        2 to "2",
                        4 to "4",
                        6 to "6",
                        8 to "8"
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        precisionOptions.forEach { (value, label) ->
                            val isSelected = settings.decimalPrecision == value
                            FilterChip(
                                selected = isSelected,
                                onClick = { onSetPrecision(value) },
                                label = {
                                    Text(
                                        text = label,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = OrangeOperator,
                                    selectedLabelColor = TextWhite,
                                    containerColor = if (isDarkTheme) FunctionLightGray else LightFunctionBg,
                                    labelColor = if (isDarkTheme) TextWhite else LightTextPrimary
                                ),
                                modifier = Modifier.testTag("precision_chip_$label")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(
                        color = if (isDarkTheme) FunctionLightGray.copy(alpha = 0.5f) else LightFunctionBg
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Thousands Separator
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Thousands Separator",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isDarkTheme) TextWhite else LightTextPrimary
                            )
                            Text(
                                text = "Format numbers with commas (e.g. 1,000,000)",
                                fontSize = 12.sp,
                                color = if (isDarkTheme) TextMutedGray else LightTextSecondary
                            )
                        }
                        Switch(
                            checked = settings.thousandsSeparator,
                            onCheckedChange = onSetThousandsSeparator,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = TextWhite,
                                checkedTrackColor = OrangeOperator
                            ),
                            modifier = Modifier.testTag("switch_thousands_separator")
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsCard(
    title: String,
    icon: ImageVector,
    isDarkTheme: Boolean,
    content: @Composable () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkTheme) NumberDarkGray else LightFunctionBg.copy(alpha = 0.5f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = OrangeOperator,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = OrangeOperator
                )
            }
            content()
        }
    }
}

@Composable
private fun ThemeOptionButton(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val bg = if (selected) OrangeOperator else if (isDarkTheme) FunctionLightGray else LightFunctionBg
    val contentColor = if (selected) TextWhite else if (isDarkTheme) TextMutedGray else LightTextPrimary

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = contentColor
            )
        }
    }
}
