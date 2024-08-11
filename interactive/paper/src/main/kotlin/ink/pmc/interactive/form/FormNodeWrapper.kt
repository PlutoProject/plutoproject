package ink.pmc.interactive.form

import ink.pmc.interactive.api.form.GeneralFormNode
import org.geysermc.floodgate.api.player.FloodgatePlayer
import java.util.*

class FormNodeWrapper : GeneralFormNode {

    private var actual: GeneralFormNode? = null

    override val children: LinkedList<GeneralFormNode> = object : LinkedList<GeneralFormNode>() {
        override fun add(element: GeneralFormNode): Boolean {
            return if (actual == null) {
                actual = element
                true
            } else {
                super.add(element)
            }
        }

        override fun add(index: Int, element: GeneralFormNode) {
            if (actual == null) {
                actual = element
            } else {
                super.add(element)
            }
        }
    }
    override val builder: Any.() -> Unit = {}

    override fun render(player: FloodgatePlayer) {
        actual?.render(player)
    }

}