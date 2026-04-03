package com.shubhamtechie.calculator.core.common

interface ExpressionParser {
    fun evaluate(expression: String, isDegreeMode: Boolean, variables: Map<String, Double> = emptyMap()): Double
}
