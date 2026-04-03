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

    private val factorialFunction = object : Function("fact", 1) {
        override fun apply(vararg args: Double): Double {
            val arg = args[0]
            if (arg < 0 || arg != Math.floor(arg)) {
                throw IllegalArgumentException("Factorial requires a non-negative integer")
            }
            var result = 1.0
            for (i in 1..arg.toInt()) result *= i
            return result
        }
    }

    override fun evaluate(
        expression: String,
        isDegreeMode: Boolean,
        variables: Map<String, Double>
    ): Double {
        val closedExpression = autoCloseParentheses(expression)
        val processedExpression = preprocess(closedExpression)
        
        val builder = ExpressionBuilder(processedExpression)
            .functions(factorialFunction)
            .variables(variables.keys)

        if (isDegreeMode) {
            builder.functions(getDegreeFunctions())
        }

        val exp = builder.build()
        variables.forEach { (name, value) -> exp.setVariable(name, value) }

        return exp.evaluate()
    }

    private fun autoCloseParentheses(expression: String): String {
        val openCount = expression.count { it == '(' }
        val closeCount = expression.count { it == ')' }
        return expression + ")".repeat((openCount - closeCount).coerceAtLeast(0))
    }

    private fun preprocess(expression: String): String {
        var processed = expression
            .replace(" x ", " * ")
            .replace("÷", "/")
            .replace("π", "pi")
            .replace("√", "sqrt")

        // Postfix operators: replace 50% with (50/100) and 5! with fact(5)
        // Regex handles decimals for percent but only integers for factorial
        processed = processed.replace(Regex("(\\d*\\.?\\d+)%"), "($1/100)")
        processed = processed.replace(Regex("(\\d+)!"), "fact($1)")

        // Convert scientific E notation → explicit power: 1.5E10 → 1.5*10^10
        processed = processed.replace(Regex("(\\d*\\.?\\d+)[Ee]([+\\-]?\\d+)"), "$1*10^$2")
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
        val symbols = "pi|sqrt|log|sin|cos|tan|atan|asin|acos|Ans|exp|\\("
        processed = processed.replace(
            Regex("(\\d|pi|Ans|\\))\\s*($symbols|\\d)"), { matchResult ->
                val first = matchResult.groupValues[1]
                val second = matchResult.groupValues[2]
                if (first.all { it.isDigit() } && second.all { it.isDigit() }) {
                    matchResult.value
                } else {
                    "$first*$second"
                }
            }
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
