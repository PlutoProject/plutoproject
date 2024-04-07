package ink.pmc.common.misc.impl

import ink.pmc.common.misc.api.MiscAPI
import ink.pmc.common.misc.api.elevator.ElevatorManager
import ink.pmc.common.misc.api.head.HeadManager
import ink.pmc.common.misc.api.sit.SitManager

object MiscAPIImpl : MiscAPI {

    lateinit var internalSitManager: SitManager
    lateinit var internalElevatorManager: ElevatorManager
    lateinit var internalHeadManager: HeadManager

    override val sitManager: SitManager
        get() {
            if (::internalSitManager.isInitialized.not()) {
                throw RuntimeException("API not initialized")
            }

            return internalSitManager
        }
    override val elevatorManager: ElevatorManager
        get() {
            if (::internalElevatorManager.isInitialized.not()) {
                throw RuntimeException("API not initialized")
            }

            return internalElevatorManager
        }
    override val headManager: HeadManager
        get() {
            if (::internalHeadManager.isInitialized.not()) {
                throw RuntimeException("API not initialized")
            }

            return internalHeadManager
        }

}