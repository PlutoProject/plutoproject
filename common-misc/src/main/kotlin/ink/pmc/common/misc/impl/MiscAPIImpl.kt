package ink.pmc.common.misc.impl

import ink.pmc.common.misc.api.MiscAPI
import ink.pmc.common.misc.api.sit.SitManager

object MiscAPIImpl : MiscAPI {

    lateinit var internalSitManager: SitManager

    override val sitManager: SitManager
        get() {
            if (::internalSitManager.isInitialized.not()) {
                throw RuntimeException("API not initialized")
            }

            return internalSitManager
        }

}