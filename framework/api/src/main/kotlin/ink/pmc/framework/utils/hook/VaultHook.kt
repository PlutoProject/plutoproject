package ink.pmc.framework.utils.hook

import ink.pmc.framework.utils.platform.paper
import net.milkbowl.vault.economy.Economy

class VaultHook {
    val economy = paper.servicesManager.getRegistration(Economy::class.java)?.provider
}