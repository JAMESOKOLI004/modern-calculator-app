package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.FunctionLightGray
import com.example.ui.theme.LightFunctionBg
import com.example.ui.theme.LightNumberBg
import com.example.ui.theme.LightTextPrimary
import com.example.ui.theme.NumberDarkGray
import com.example.ui.theme.OrangeOperator
import com.example.ui.theme.TextWhite

enum class ButtonCategory {
    NUMBER,
    OPERATOR,
    FUNCTION
}

@Composable
fun CalculatorButton(
    symbol: String,
    category: ButtonCategory,
    isDarkTheme: Boolean,
    hapticEnabled: Boolean,
    modifier: Modifier = Modifier,
    contentDescriptionText: String = symbol,
    testTag: String = "btn_$symbol",
    onClick: () -> Unit
) {
    val haptic: HapticFeedback = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 600f),
        label = "button_scale"
    )

    val shape: Shape = when (category) {
        ButtonCategory.OPERATOR -> CircleShape // circular buttons
        ButtonCategory.NUMBER -> RoundedCornerShape(22.dp) // rounded squares
        ButtonCategory.FUNCTION -> RoundedCornerShape(22.dp) // rounded function buttons
    }

    val backgroundColor: Color = when (category) {
        ButtonCategory.OPERATOR -> OrangeOperator
        ButtonCategory.NUMBER -> if (isDarkTheme) NumberDarkGray else LightNumberBg
        ButtonCategory.FUNCTION -> if (isDarkTheme) FunctionLightGray else LightFunctionBg
    }

    val textColor: Color = when (category) {
        ButtonCategory.OPERATOR -> TextWhite
        ButtonCategory.NUMBER -> if (isDarkTheme) TextWhite else LightTextPrimary
        ButtonCategory.FUNCTION -> if (isDarkTheme) TextWhite else LightTextPrimary
    }

    val fontSize = when {
        category == ButtonCategory.OPERATOR -> 30.sp
        symbol.length > 2 -> 20.sp
        category == ButtonCategory.FUNCTION -> 22.sp
        else -> 26.sp
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(4.dp)
            .scale(scale)
            .clip(shape)
            .background(backgroundColor, shape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    if (hapticEnabled) {
                        try {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        } catch (e: Exception) {
                            // Ignore if not supported
                        }
                    }
                    onClick()
                }
            )
            .testTag(testTag)
            .semantics {
                contentDescription = contentDescriptionText
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = symbol,
            color = textColor,
            fontSize = fontSize,
            fontWeight = if (category == ButtonCategory.OPERATOR) FontWeight.Bold else FontWeight.SemiBold
        )
    }
}
