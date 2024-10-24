package ink.pmc.transfer

import ink.pmc.framework.utils.multiplaform.item.KeyedMaterial
import net.kyori.adventure.text.Component

class CategoryImpl(
    override val id: String,
    override var playerCount: Int,
    override val icon: KeyedMaterial,
    override val name: Component,
    override val description: List<Component>
) : AbstractCategory()