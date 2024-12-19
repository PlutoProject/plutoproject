package ink.pmc.framework.hook

import ink.pmc.framework.platform.paper
import net.milkbowl.vault.economy.Economy

class VaultHook {
    val economy = paper.servicesManager.getRegistration(Economy::class.java)?.provider
}