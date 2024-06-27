package ink.pmc.utils.dsl

import ink.pmc.advkt.component.RootComponentKt
import ink.pmc.utils.structure.Builder
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

class ItemStackDsl(private var material: Material, private val amount: Int) : Builder<ItemStack> {

    var displayName: Component? = null
    private val lore = mutableListOf<Component>()
    private val enchantments = mutableMapOf<Enchantment, Int>()
    private val itemFlags = mutableSetOf<ItemFlag>()

    fun displayName(component: RootComponentKt.() -> Unit) {
        displayName = RootComponentKt().apply(component).build()
    }

    fun lore(component: Component) {
        lore.add(component)
    }

    fun lore(component: RootComponentKt.() -> Unit) {
        lore.add(RootComponentKt().apply(component).build())
    }

    fun lore(components: Collection<Component>) {
        lore.addAll(components)
    }

    fun enchantment(enchantment: Enchantment, level: Int) {
        enchantments[enchantment] = level
    }

    fun enchantment(map: Map<Enchantment, Int>) {
        enchantments.putAll(map)
    }

    fun itemFlag(itemFlag: ItemFlag) {
        itemFlags.add(itemFlag)
    }

    fun itemFlag(vararg itemFlags: ItemFlag) {
        this.itemFlags.addAll(itemFlags)
    }

    fun itemFlag(itemFlags: Collection<ItemFlag>) {
        this.itemFlags.addAll(itemFlags)
    }

    override fun build(): ItemStack {
        return ItemStack(material, amount)
            .apply {
                editMeta {
                    it.displayName(displayName)
                    it.lore(this@ItemStackDsl.lore)
                }
                addEnchantments(this@ItemStackDsl.enchantments)
                addItemFlags(*this@ItemStackDsl.itemFlags.toTypedArray())
            }
    }

}

fun itemStack(material: Material, amount: Int = 1, block: ItemStackDsl.() -> Unit): ItemStack {
    return ItemStackDsl(material, amount).apply(block).build()
}