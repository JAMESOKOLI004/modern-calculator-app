package com.example.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class ThemeMode {
    DARK,
    LIGHT,
    SYSTEM
}

data class CalculatorSettings(
    val themeMode: ThemeMode = ThemeMode.DARK,
    val hapticFeedback: Boolean = true,
    val decimalPrecision: Int = -1, // -1 means "Auto"
    val thousandsSeparator: Boolean = true
)

class SettingsManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("calculator_settings", Context.MODE_PRIVATE)

    private val _settings = MutableStateFlow(loadSettings())
    val settings: StateFlow<CalculatorSettings> = _settings.asStateFlow()

    private fun loadSettings(): CalculatorSettings {
        val themeString = prefs.getString(KEY_THEME_MODE, ThemeMode.DARK.name) ?: ThemeMode.DARK.name
        val themeMode = try {
            ThemeMode.valueOf(themeString)
        } catch (e: Exception) {
            ThemeMode.DARK
        }
        val haptic = prefs.getBoolean(KEY_HAPTIC, true)
        val precision = prefs.getInt(KEY_PRECISION, -1)
        val separator = prefs.getBoolean(KEY_SEPARATOR, true)
        return CalculatorSettings(
            themeMode = themeMode,
            hapticFeedback = haptic,
            decimalPrecision = precision,
            thousandsSeparator = separator
        )
    }

    fun setThemeMode(themeMode: ThemeMode) {
        prefs.edit().putString(KEY_THEME_MODE, themeMode.name).apply()
        _settings.value = _settings.value.copy(themeMode = themeMode)
    }

    fun setHapticFeedback(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_HAPTIC, enabled).apply()
        _settings.value = _settings.value.copy(hapticFeedback = enabled)
    }

    fun setDecimalPrecision(precision: Int) {
        prefs.edit().putInt(KEY_PRECISION, precision).apply()
        _settings.value = _settings.value.copy(decimalPrecision = precision)
    }

    fun setThousandsSeparator(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SEPARATOR, enabled).apply()
        _settings.value = _settings.value.copy(thousandsSeparator = enabled)
    }

    companion object {
        private const val KEY_THEME_MODE = "key_theme_mode"
        private const val KEY_HAPTIC = "key_haptic"
        private const val KEY_PRECISION = "key_precision"
        private const val KEY_SEPARATOR = "key_separator"
    }
}
