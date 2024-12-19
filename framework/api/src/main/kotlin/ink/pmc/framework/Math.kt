package ink.pmc.framework

import java.math.BigDecimal
import java.math.RoundingMode

fun Double.roundToTwoDecimals(): Double {
    return BigDecimal(this).setScale(2, RoundingMode.HALF_EVEN).toDouble()
}

fun Double.trimmed(): String {
    // 将 Double 转换为字符串并去除末尾多余的 0
    var result = toBigDecimal().stripTrailingZeros().toPlainString()

    // 如果最后一位是 '.'，去除它
    if (result.endsWith(".")) {
        result = result.dropLast(1)
    }

    return result
}