package ink.pmc.visual.api

interface GenericRenderer<P, T> {

    fun render(player: P, obj: T)

}