package ink.pmc.common.utils

import java.math.BigDecimal
import java.math.RoundingMode

val Double.roundTwoDecimals: Double
    get() = BigDecimal(this).setScale(2, RoundingMode.HALF_EVEN).toDouble()