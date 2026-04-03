package com.shubhamtechie.calculator.feature.calculator.domain.usecase

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import javax.inject.Inject
import kotlin.math.abs

class FormatResultUseCase @Inject constructor() {
    operator fun invoke(result: Double, isScientific: Boolean): String {
        if (result.isNaN()) return "Error"
        if (result.isInfinite()) return "∞"

        val symbols = DecimalFormatSymbols(Locale.US)
        val pattern = if (isScientific || abs(result) >= 1e10 || (abs(result) < 1e-7 && result != 0.0)) {
            "0.######E0"
        } else {
            "0.##########"
        }

        return DecimalFormat(pattern, symbols).format(result)
    }
}
