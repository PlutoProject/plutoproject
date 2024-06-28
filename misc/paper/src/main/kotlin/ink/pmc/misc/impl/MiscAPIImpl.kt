package ink.pmc.misc.impl

import ink.pmc.misc.api.MiscAPI
import ink.pmc.misc.api.elevator.ElevatorManager
import ink.pmc.misc.api.head.HeadManager
import ink.pmc.misc.api.sit.SitManager

object MiscAPIImpl : MiscAPI {

    lateinit var internalSitManager: SitManager
    lateinit var internalElevatorManager: ElevatorManager
    lateinit var internalHeadManager: HeadManager

    override val sitManager: SitManager
        get() {
            if (MiscAPIImpl::internalSitManager.isInitialized.not()) {
                throw RuntimeException("API not initialized")
            }

            return internalSitManager
        }
    override val elevatorManager: ElevatorManager
        get() {
            if (MiscAPIImpl::internalElevatorManager.isInitialized.not()) {
                throw RuntimeException("API not initialized")
            }

            return internalElevatorManager
        }
    override val headManager: HeadManager
        get() {
            if (MiscAPIImpl::internalHeadManager.isInitialized.not()) {
                throw RuntimeException("API not initialized")
            }

            return internalHeadManager
        }
}