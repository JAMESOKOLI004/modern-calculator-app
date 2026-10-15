package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Backspace
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.FunctionLightGray
import com.example.ui.theme.LightFunctionBg
import com.example.ui.theme.LightTextSecondary
import com.example.ui.theme.OrangeOperator
import com.example.ui.theme.TextMutedGray
import com.example.ui.theme.TextWhite

@Composable
fun DisplayArea(
    expression: String,
    result: String,
    previewResult: String?,
    historyCount: Int,
    isDarkTheme: Boolean,
    onSwipeDown: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenSettings: () -> Unit,
    onBackspace: () -> Unit,
    modifier: Modifier = Modifier
) {
    val exprScrollState = rememberScrollState()
    val resultScrollState = rememberScrollState()

    // Scroll to end when expression or result updates
    LaunchedEffect(expression) {
        exprScrollState.animateScrollTo(exprScrollState.maxValue)
    }
    LaunchedEffect(result) {
        resultScrollState.animateScrollTo(resultScrollState.maxValue)
    }

    var verticalDragAccumulator by remember { mutableFloatStateOf(0f) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onDragStart = { verticalDragAccumulator = 0f },
                    onDragEnd = {
                        if (verticalDragAccumulator > 50f) {
                            onSwipeDown()
                        }
                        verticalDragAccumulator = 0f
                    },
                    onDragCancel = { verticalDragAccumulator = 0f },
                    onVerticalDrag = { change, dragAmount ->
                        change.consume()
                        verticalDragAccumulator += dragAmount
                        if (verticalDragAccumulator > 80f) {
                            onSwipeDown()
                            verticalDragAccumulator = 0f
                        }
                    }
                )
            }
            .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Bottom
    ) {
        // Top row: History drag affordance and settings
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Swipe down handle / button
            Surface(
                onClick = onOpenHistory,
                shape = RoundedCornerShape(16.dp),
                color = if (isDarkTheme) FunctionLightGray.copy(alpha = 0.6f) else LightFunctionBg,
                modifier = Modifier.testTag("history_pull_button")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BadgedBox(
                        badge = {
                            if (historyCount > 0) {
                                Badge(
                                    containerColor = OrangeOperator,
                                    contentColor = TextWhite
                                ) {
                                    Text(text = if (historyCount > 99) "99+" else "$historyCount")
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "History",
                            modifier = Modifier.size(18.dp),
                            tint = if (isDarkTheme) TextMutedGray else LightTextSecondary
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "History",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isDarkTheme) TextMutedGray else LightTextSecondary
                    )
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Swipe down to reveal history",
                        modifier = Modifier.size(16.dp),
                        tint = if (isDarkTheme) TextMutedGray else LightTextSecondary
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (result.length > 1 || (result != "0" && result.isNotEmpty()) || expression.isNotEmpty()) {
                    IconButton(
                        onClick = onBackspace,
                        modifier = Modifier.testTag("btn_backspace_quick")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Backspace,
                            contentDescription = "Backspace",
                            tint = if (isDarkTheme) TextMutedGray else LightTextSecondary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                IconButton(
                    onClick = onOpenSettings,
                    modifier = Modifier.testTag("btn_settings")
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = if (isDarkTheme) TextMutedGray else LightTextSecondary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Current expression (small, gray, right-aligned)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(exprScrollState),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = expression.ifEmpty { " " },
                fontSize = 20.sp,
                fontWeight = FontWeight.Normal,
                color = if (isDarkTheme) TextMutedGray else LightTextSecondary,
                textAlign = TextAlign.End,
                maxLines = 1,
                modifier = Modifier.testTag("display_expression")
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Main Result / Entered Number (large, white, bold, right-aligned)
        val displayFontSize = when {
            result.length > 14 -> 36.sp
            result.length > 9 -> 44.sp
            else -> 56.sp
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(resultScrollState),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = result,
                fontSize = displayFontSize,
                fontWeight = FontWeight.Bold,
                color = if (isDarkTheme) TextWhite else MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.End,
                maxLines = 1,
                modifier = Modifier.testTag("display_result")
            )
        }

        // Live preview if available and not redundant
        if (previewResult != null && previewResult != result) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "= $previewResult",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = OrangeOperator.copy(alpha = 0.9f),
                textAlign = TextAlign.End,
                maxLines = 1,
                modifier = Modifier.testTag("display_preview")
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Gesture hint pill bar
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(36.dp)
                .height(4.dp)
                .clip(CircleShape)
                .background(
                    if (isDarkTheme) FunctionLightGray.copy(alpha = 0.5f) else LightFunctionBg
                )
        )
    }
}
