package ink.pmc.interactive.gui.form

import ink.pmc.interactive.api.gui.form.GeneralFormNode
import ink.pmc.interactive.api.gui.form.RootFormNode
import org.geysermc.floodgate.api.player.FloodgatePlayer
import java.util.*

class FormNodeWrapper : GeneralFormNode {

    private var actual: GeneralFormNode? = null

    override val children: LinkedList<GeneralFormNode> = object : LinkedList<GeneralFormNode>() {
        override fun add(element: GeneralFormNode): Boolean {
            return if (element is RootFormNode<*, *, *>) {
                actual = element
                true
            } else {
                super.add(element)
            }
        }

        override fun add(index: Int, element: GeneralFormNode) {
            if (element is RootFormNode<*, *, *>) {
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