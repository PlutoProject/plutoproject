package ink.pmc.framework.config

import com.electronwill.nightconfig.core.Config
import com.electronwill.nightconfig.core.Config.Entry

fun Collection<Entry>.toMap(): Map<String, Any?> {
    return mutableMapOf<String, Any?>().apply {
        putAll(this@toMap.map { it.key to it.getValue() })
    }
}

fun Config.toMapViaEntry(): Map<String, Any?> {
    return entrySet().toMap()
}