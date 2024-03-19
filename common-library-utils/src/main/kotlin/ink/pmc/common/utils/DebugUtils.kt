package ink.pmc.common.utils

import java.util.logging.Logger

private val logger = Logger.getLogger("DEBUG MODE CHECK")

@Suppress("UNUSED")
fun isInDebugMode(): Boolean {
    if (System.getProperty("PLUTO_DEBUG_MODE") != null) {
        logger.warning("A plugin triggered the debug mode check method.")
        logger.warning("The server is running debug mode in Gradle test plugin.")
        logger.warning("Some logic will be disabled.")

        return true
    }

    return false
}