package com.example.ui

import android.text.format.DateUtils
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CalculationEntity
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryBottomSheet(
    sheetState: SheetState,
    historyList: List<CalculationEntity>,
    isDarkTheme: Boolean,
    onDismiss: () -> Unit,
    onReuseExpression: (String) -> Unit,
    onUseResult: (String) -> Unit,
    onClearHistory: () -> Unit,
    onDeleteItem: (CalculationEntity) -> Unit
) {
    var showClearConfirmDialog by remember { mutableStateOf(false) }

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
        modifier = Modifier.testTag("history_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.75f)
                .padding(horizontal = 20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = OrangeOperator,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Calculation History",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkTheme) TextWhite else LightTextPrimary
                    )
                    if (historyList.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(OrangeOperator.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${historyList.size}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = OrangeOperator
                            )
                        }
                    }
                }

                if (historyList.isNotEmpty()) {
                    TextButton(
                        onClick = { showClearConfirmDialog = true },
                        modifier = Modifier.testTag("btn_clear_history")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Clear History",
                            tint = OrangeOperator,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Clear All",
                            color = OrangeOperator,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (historyList.isEmpty()) {
                // Empty state
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isDarkTheme) NumberDarkGray else LightFunctionBg
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Calculate,
                                contentDescription = null,
                                tint = OrangeOperator,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No History Yet",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isDarkTheme) TextWhite else LightTextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Your completed calculations will appear here.",
                            fontSize = 14.sp,
                            color = if (isDarkTheme) TextMutedGray else LightTextSecondary
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(historyList, key = { it.id }) { item ->
                        HistoryItemCard(
                            item = item,
                            isDarkTheme = isDarkTheme,
                            onReuseExpression = {
                                onReuseExpression(item.expression)
                                onDismiss()
                            },
                            onUseResult = {
                                onUseResult(item.result)
                                onDismiss()
                            },
                            onDelete = { onDeleteItem(item) }
                        )
                    }
                }
            }
        }
    }

    if (showClearConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showClearConfirmDialog = false },
            title = { Text(text = "Clear History?") },
            text = { Text(text = "Are you sure you want to delete all past calculation records? This cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onClearHistory()
                        showClearConfirmDialog = false
                    },
                    modifier = Modifier.testTag("btn_confirm_clear_history")
                ) {
                    Text(text = "Clear All", color = OrangeOperator, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirmDialog = false }) {
                    Text(text = "Cancel")
                }
            }
        )
    }
}

@Composable
fun HistoryItemCard(
    item: CalculationEntity,
    isDarkTheme: Boolean,
    onReuseExpression: () -> Unit,
    onUseResult: () -> Unit,
    onDelete: () -> Unit
) {
    val relativeTime = try {
        DateUtils.getRelativeTimeSpanString(
            item.timestamp,
            System.currentTimeMillis(),
            DateUtils.MINUTE_IN_MILLIS
        ).toString()
    } catch (e: Exception) {
        ""
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkTheme) NumberDarkGray else LightFunctionBg.copy(alpha = 0.5f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("history_item_${item.id}")
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = relativeTime,
                    fontSize = 11.sp,
                    color = if (isDarkTheme) TextMutedGray.copy(alpha = 0.7f) else LightTextSecondary
                )
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete item",
                        tint = if (isDarkTheme) TextMutedGray.copy(alpha = 0.6f) else LightTextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Expression
            Text(
                text = item.expression,
                fontSize = 15.sp,
                color = if (isDarkTheme) TextMutedGray else LightTextSecondary,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Result
            Text(
                text = "= ${item.result}",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDarkTheme) TextWhite else LightTextPrimary
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Tap to reuse action chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                SuggestionChip(
                    onClick = onReuseExpression,
                    label = { Text("Reuse Expr", fontSize = 12.sp) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Restore,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                    },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        labelColor = if (isDarkTheme) TextWhite else LightTextPrimary
                    ),
                    modifier = Modifier.padding(end = 6.dp)
                )

                SuggestionChip(
                    onClick = onUseResult,
                    label = { Text("Use Result", fontSize = 12.sp, color = OrangeOperator) },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        labelColor = OrangeOperator
                    )
                )
            }
        }
    }
}
