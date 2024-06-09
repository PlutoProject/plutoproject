package ink.pmc.visual.api

interface Renderer<P, T> {

    fun render(player: P, obj: T)

}