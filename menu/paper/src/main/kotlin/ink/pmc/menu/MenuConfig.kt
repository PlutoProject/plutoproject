package ink.pmc.menu

data class MenuConfig(
    val prebuiltPages: PrebuiltPages = PrebuiltPages(),
    val prebuiltButtons: PrebuiltButtons = PrebuiltButtons(),
    val pages: List<Page> = listOf()
)

data class PrebuiltPages(
    val home: Boolean = false,
    val assistant: Boolean = false,
)

data class PrebuiltButtons(
    val coreprotectLookup: Boolean = false
)

data class Page(
    val id: String,
    val patterns: List<String> = listOf(),
    val buttons: List<Button> = listOf()
)

data class Button(
    val id: String,
    val pattern: Char
)