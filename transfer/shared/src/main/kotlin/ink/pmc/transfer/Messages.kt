package ink.pmc.transfer

import ink.pmc.advkt.component.component
import ink.pmc.advkt.component.text
import ink.pmc.utils.visual.mochaMaroon
import ink.pmc.utils.visual.mochaText

val DESTINATION_NOT_EXISTED = component {
    text("无法传送，名为 ") with mochaMaroon
    text("<name> ") with mochaText
    text("的服务器未找到") with mochaMaroon
}