package com.shubhamtechie.calculator.core.common

import net.objecthunter.exp4j.ExpressionBuilder
import net.objecthunter.exp4j.function.Function
import net.objecthunter.exp4j.operator.Operator
import kotlin.math.*

class ExpressionParser {

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
            for (i in 1..arg) {
                result *= i
            }
            return result
        }
    }

    fun evaluate(
        expression: String,
        isDegreeMode: Boolean,
        variables: Map<String, Double> = emptyMap()
    ): Double {
        val processedExpression = preprocess(expression)
        val builder = ExpressionBuilder(processedExpression)
            .operator(factorial)
            .variables(variables.keys)

        if (isDegreeMode) {
            builder.functions(getDegreeFunctions())
        }

        val exp = builder.build()
        variables.forEach { (name, value) ->
            exp.setVariable(name, value)
        }

        return exp.evaluate()
    }

    private fun preprocess(expression: String): String {
        // Replace symbols and handle functions
        var processed = expression
            .replace(" x ", " * ")   // multiplication operator
            .replace("÷", "/")
            .replace("π", "pi")       // pi constant
            .replace("√", "sqrt")     // sqrt function

        // Handle ln( and log( carefully
        processed = buildString {
            var i = 0
            while (i < processed.length) {
                when {
                    processed.startsWith("ln(", i) -> {
                        append("log(")
                        i += 3
                    }
                    processed.startsWith("log(", i) -> {
                        append("log10(")
                        i += 4
                    }
                    else -> {
                        append(processed[i])
                        i++
                    }
                }
            }
        }

        // Handle implicit multiplication (e.g., 2pi, 5(3+1), (2+1)sin(30))
        // Regex to find: digit/pi followed by pi/sqrt/log/sin/cos/tan/(
        processed = processed.replace(Regex("(\\d|pi)\\s*(pi|sqrt|log|sin|cos|tan|atan|asin|acos|\\()"), "$1*$2")
        // Regex to find: ) followed by digit/pi/sqrt/log/sin/cos/tan/(
        processed = processed.replace(Regex("\\)\\s*(\\d|pi|sqrt|log|sin|cos|tan|atan|asin|acos|\\()"), ")*$1")

        return processed
    }

    private fun getDegreeFunctions(): List<Function> {
        return listOf(
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
}
