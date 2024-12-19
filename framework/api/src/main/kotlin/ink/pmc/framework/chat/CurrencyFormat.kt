package ink.pmc.framework.chat

import java.text.DecimalFormat

fun Double.currencyFormat(): String {
    val decimalFormat = DecimalFormat("#,##0.00")
    return decimalFormat.format(this)
}