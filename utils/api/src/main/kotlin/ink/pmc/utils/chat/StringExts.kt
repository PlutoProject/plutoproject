package ink.pmc.utils.chat

private const val VALID_IDENTIFIER_REGEX = "^[a-zA-Z0-9_]*$"

val String.isValidIdentifier: Boolean
    get() = matches(VALID_IDENTIFIER_REGEX.toRegex())