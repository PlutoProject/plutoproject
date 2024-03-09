package ink.pmc.common.member

import org.bukkit.plugin.java.JavaPlugin

@Suppress("UNUSED")
class MemberPlugin : JavaPlugin() {

    override fun onEnable() {
        logger.info("common-member now enabled.")
    }

    override fun onDisable() {
        logger.info("common-member now disabled.")
    }

}