package ink.pmc.common.misc.api

import ink.pmc.common.misc.api.sit.SitManager

val SitManager: SitManager = MiscAPI.instance.sitManager

interface MiscAPI {

    companion object {
        lateinit var instance: MiscAPI
    }

    val sitManager: SitManager

}