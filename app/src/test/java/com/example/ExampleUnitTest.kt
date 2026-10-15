package com.example

import com.example.domain.CalculatorEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal

class ExampleUnitTest {

    @Test
    fun testBasicAddition() {
        val res = CalculatorEngine.evaluate("12 + 8")
        assertTrue(res is CalculatorEngine.EvaluationResult.Success)
        assertEquals("20", (res as CalculatorEngine.EvaluationResult.Success).formatted)
    }

    @Test
    fun testOperatorPrecedence() {
        val res = CalculatorEngine.evaluate("2 + 3 × 4")
        assertTrue(res is CalculatorEngine.EvaluationResult.Success)
        assertEquals("14", (res as CalculatorEngine.EvaluationResult.Success).formatted)
    }

    @Test
    fun testDivisionByZero() {
        val res = CalculatorEngine.evaluate("42 ÷ 0")
        assertTrue(res is CalculatorEngine.EvaluationResult.Error)
        assertEquals("Cannot divide by 0", (res as CalculatorEngine.EvaluationResult.Error).message)
    }

    @Test
    fun testPercentageCalculation() {
        val res = CalculatorEngine.evaluate("200 + 10%")
        assertTrue(res is CalculatorEngine.EvaluationResult.Success)
        assertEquals("220", (res as CalculatorEngine.EvaluationResult.Success).formatted)
    }

    @Test
    fun testDecimalPrecisionFormatting() {
        val value = BigDecimal("123.456789")
        val formatted = CalculatorEngine.formatNumber(value, precision = 2, useThousandsSeparator = true)
        assertEquals("123.46", formatted)
    }

    @Test
    fun testThousandsSeparator() {
        val value = BigDecimal("1000000")
        val formatted = CalculatorEngine.formatNumber(value, precision = -1, useThousandsSeparator = true)
        assertEquals("1,000,000", formatted)
    }
}
