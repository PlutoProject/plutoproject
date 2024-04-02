package ink.pmc.common

import ink.pmc.common.container.Container
import ink.pmc.common.dsl.ContainerDSL
import ink.pmc.common.inputhandler.PageInputHandler
import ink.pmc.common.renderer.PageRenderer

@Suppress("UNUSED")
interface ContainerManager {

    fun createContainer(block: ContainerDSL.() -> Unit): Container

    fun getDefaultRenderer(type: Class<*>): PageRenderer

    fun registerDefaultRender(type: Class<*>, render: PageRenderer)

    fun getDefaultInputHandler(type: Class<*>): PageInputHandler

    fun registerDefaultInputHandler(type: Class<*>, inputHandler: PageInputHandler)

}