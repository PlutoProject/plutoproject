package ink.pmc.framework.bridge

import ink.pmc.framework.FrameworkConfig
import org.koin.java.KoinJavaComponent.getKoin
import java.util.logging.Level
import java.util.logging.Logger

private val config by lazy { getKoin().get<FrameworkConfig>().bridge }
private val logger = Logger.getLogger("BridgeDebug")

fun debugInfo(message: String) {
    if (!config.debug) return
    logger.info(message)
}

fun debugWarn(message: String, e: Throwable? = null) {
    if (!config.debug) return
    if (e != null) {
        logger.log(Level.WARNING, message, e)
    } else {
        logger.warning(message)
    }
}

fun debugError(message: String, e: Throwable? = null) {
    if (!config.debug) return
    if (e != null) {
        logger.log(Level.SEVERE, message, e)
    } else {
        logger.severe(message)
    }
}