package ink.pmc.framework.utils.chat

import java.text.DecimalFormat

private const val VALID_IDENTIFIER_REGEX = "^[a-zA-Z0-9_]*$"

val String.isValidIdentifier: Boolean
    get() = matches(VALID_IDENTIFIER_REGEX.toRegex()) && this.isNotEmpty()

fun Double.currencyFormat(): String {
    val decimalFormat = DecimalFormat("#,##0.00")
    return decimalFormat.format(this)
}