package ink.pmc.essentials.recipes

import ink.pmc.essentials.items.NOTEBOOK_ITEM
import ink.pmc.essentials.plugin
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.inventory.recipe.CraftingBookCategory

private val key = NamespacedKey(plugin, "notebook")

val NOTEBOOK_RECIPE = ShapedRecipe(key, NOTEBOOK_ITEM)
    .apply {
        shape("FFF", "FBF", "FFF")
        setIngredient('F', Material.FEATHER)
        setIngredient('B', Material.BOOK)
        category = CraftingBookCategory.EQUIPMENT
    }