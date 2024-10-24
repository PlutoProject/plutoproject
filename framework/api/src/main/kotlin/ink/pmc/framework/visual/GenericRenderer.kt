package ink.pmc.framework.visual

interface GenericRenderer<P, T> {

    fun render(player: P, obj: T)

}