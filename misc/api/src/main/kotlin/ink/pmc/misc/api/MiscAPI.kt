package ink.pmc.misc.api

import ink.pmc.misc.api.elevator.ElevatorManager
import ink.pmc.misc.api.head.HeadManager
import ink.pmc.misc.api.sit.SitManager

val SitManager: SitManager = MiscAPI.instance.sitManager
val ElevatorManager: ElevatorManager = MiscAPI.instance.elevatorManager

interface MiscAPI {

    companion object {
        lateinit var instance: MiscAPI
    }

    val sitManager: SitManager
    val elevatorManager: ElevatorManager
    val headManager: HeadManager
}