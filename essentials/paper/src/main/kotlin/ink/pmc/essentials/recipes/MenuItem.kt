package ink.pmc.essentials.recipes

import ink.pmc.essentials.items.MENU_ITEM
import ink.pmc.essentials.plugin
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.inventory.recipe.CraftingBookCategory

private val key = NamespacedKey(plugin, "menu_item")

val MENU_ITEM_RECIPE = ShapedRecipe(key, MENU_ITEM)
    .apply {
        shape("FFF", "FBF", "FFF")
        setIngredient('F', Material.FEATHER)
        setIngredient('B', Material.BOOK)
        category = CraftingBookCategory.EQUIPMENT
    }