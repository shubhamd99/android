package com.shubhamtechie.calculator.feature.calculator.domain.usecase

import com.shubhamtechie.calculator.core.common.ExpressionParser
import com.shubhamtechie.calculator.core.common.ResultWrapper
import com.shubhamtechie.calculator.feature.calculator.domain.model.AngleMode
import javax.inject.Inject

class EvaluateExpressionUseCase @Inject constructor(
    private val parser: ExpressionParser
) {
    operator fun invoke(
        expression: String, 
        angleMode: AngleMode, 
        lastResult: String
    ): ResultWrapper<Double> {
        return try {
            val variables = if (lastResult.isNotEmpty() && lastResult != "Error") {
                mapOf("Ans" to lastResult.toDouble())
            } else emptyMap()
            
            val result = parser.evaluate(
                expression = expression, 
                isDegreeMode = angleMode == AngleMode.DEG,
                variables = variables
            )
            ResultWrapper.Success(result)
        } catch (e: Exception) {
            ResultWrapper.Error("Error")
        }
    }
}
