package ink.pmc.common.misc.api

import ink.pmc.common.misc.api.elevator.ElevatorManager
import ink.pmc.common.misc.api.sit.SitManager

val SitManager: SitManager = MiscAPI.instance.sitManager
val ElevatorManager: ElevatorManager = MiscAPI.instance.elevatorManager

interface MiscAPI {

    companion object {
        lateinit var instance: MiscAPI
    }

    val sitManager: SitManager
    val elevatorManager: ElevatorManager

}