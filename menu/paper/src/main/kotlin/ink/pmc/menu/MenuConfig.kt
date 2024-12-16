package ink.pmc.menu

data class MenuConfig(
    val prebuiltPages: PrebuiltPages = PrebuiltPages(),
    val prebuiltIngredients: PrebuiltIngredients = PrebuiltIngredients(),
    val pages: List<Page> = listOf()
)

data class PrebuiltPages(
    val home: Boolean = false,
    val assistant: Boolean = false,
)

data class PrebuiltIngredients(
    val coreprotectLookup: Boolean = false
)

data class Page(
    val id: String,
    val patterns: List<String> = listOf(),
    val ingredients: List<Ingredient> = listOf()
)

data class Ingredient(
    val id: String,
    val pattern: Char
)