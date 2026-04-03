package com.shubhamtechie.calculator.core.common

import net.objecthunter.exp4j.ExpressionBuilder
import net.objecthunter.exp4j.function.Function
import net.objecthunter.exp4j.operator.Operator
import javax.inject.Inject
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

class DefaultExpressionParser @Inject constructor() : ExpressionParser {

    private val factorial = object : Operator("!", 1, true, Operator.PRECEDENCE_POWER + 1) {
        override fun apply(vararg args: Double): Double {
            val arg = args[0].toInt()
            if (arg.toDouble() != args[0]) {
                throw IllegalArgumentException("Operand for factorial must be an integer")
            }
            if (arg < 0) {
                throw IllegalArgumentException("Factorial operand cannot be negative")
            }
            var result = 1.0
            for (i in 1..arg) result *= i
            return result
        }
    }

    // Postfix % operator: 50% → 0.5
    private val percent = object : Operator("%", 1, true, Operator.PRECEDENCE_POWER - 1) {
        override fun apply(vararg args: Double): Double = args[0] / 100.0
    }

    override fun evaluate(
        expression: String,
        isDegreeMode: Boolean,
        variables: Map<String, Double>
    ): Double {
        val processedExpression = preprocess(expression)
        val builder = ExpressionBuilder(processedExpression)
            .operator(factorial, percent)
            .variables(variables.keys)

        if (isDegreeMode) {
            builder.functions(getDegreeFunctions())
        }

        val exp = builder.build()
        variables.forEach { (name, value) -> exp.setVariable(name, value) }

        return exp.evaluate()
    }

    private fun preprocess(expression: String): String {
        var processed = expression
            .replace(" x ", " * ")
            .replace("÷", "/")
            .replace("π", "pi")
            .replace("√", "sqrt")

        // Convert scientific E notation → explicit power: 1.5E10 → 1.5*10^10
        // Must run before implicit-multiplication to avoid double-processing
        processed = processed.replace(Regex("(\\d)[Ee]([+\\-]?\\d)"), "$1*10^$2")
        // Strip any E with no following digit (incomplete notation: "5E" → "5")
        processed = processed.replace(Regex("[Ee](?![+\\-]?\\d)"), "")

        // Replace ln( → log( and log( → log10( (exp4j uses log for natural log)
        processed = buildString {
            var i = 0
            while (i < processed.length) {
                when {
                    processed.startsWith("ln(", i) -> { append("log("); i += 3 }
                    processed.startsWith("log(", i) -> { append("log10("); i += 4 }
                    else -> { append(processed[i]); i++ }
                }
            }
        }

        // Implicit multiplication: 2pi → 2*pi, 5(3+1) → 5*(3+1), )sin( → )*sin(
        processed = processed.replace(
            Regex("(\\d|pi)\\s*(pi|sqrt|log|sin|cos|tan|atan|asin|acos|\\()"), "$1*$2"
        )
        processed = processed.replace(
            Regex("\\)\\s*(\\d|pi|sqrt|log|sin|cos|tan|atan|asin|acos|\\()"), ")*$1"
        )

        return processed
    }

    private fun getDegreeFunctions(): List<Function> = listOf(
        object : Function("sin", 1) {
            override fun apply(vararg args: Double): Double = sin(Math.toRadians(args[0]))
        },
        object : Function("cos", 1) {
            override fun apply(vararg args: Double): Double = cos(Math.toRadians(args[0]))
        },
        object : Function("tan", 1) {
            override fun apply(vararg args: Double): Double = tan(Math.toRadians(args[0]))
        },
        object : Function("asin", 1) {
            override fun apply(vararg args: Double): Double = Math.toDegrees(asin(args[0]))
        },
        object : Function("acos", 1) {
            override fun apply(vararg args: Double): Double = Math.toDegrees(acos(args[0]))
        },
        object : Function("atan", 1) {
            override fun apply(vararg args: Double): Double = Math.toDegrees(atan(args[0]))
        }
    )
}
