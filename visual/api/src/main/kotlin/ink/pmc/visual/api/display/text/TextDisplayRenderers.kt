package ink.pmc.visual.api.display.text

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

object DefaultTextDisplayRenderer: TextDisplayRenderer by object : KoinComponent {
    val instance by inject<TextDisplayRenderer>(named("nms"))
}.instance