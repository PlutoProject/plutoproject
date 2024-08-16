package ink.pmc.menu

import net.coreprotect.config.ConfigHandler
import org.bukkit.entity.Player

const val CO_NEAR_COMMAND = "coreprotect:co near"

var Player.inspecting: Boolean
    set(value) {
        if (value) {
            ConfigHandler.inspecting[name] = true
        } else {
            ConfigHandler.inspecting.remove(name)
        }
    }
    get() = ConfigHandler.inspecting.getOrDefault(name, false)