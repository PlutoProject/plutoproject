package ink.pmc.visual.api.display.text

object DefaultTextDisplayRenderer: TextDisplayRenderer by TextDisplayRenderers.defaultRenderer

object TextDisplayRenderers {

    lateinit var defaultRenderer: TextDisplayRenderer

}