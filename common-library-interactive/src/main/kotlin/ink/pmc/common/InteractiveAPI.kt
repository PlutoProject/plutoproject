package ink.pmc.common

@Suppress("UNUSED")
interface InteractiveAPI {

    companion object {
        lateinit var instance: InteractiveAPI
    }

    val containerManager: ContainerManager

}