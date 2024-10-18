package ink.pmc.essentials.hooks

import ink.pmc.utils.platform.paper
import net.milkbowl.vault.economy.Economy

class EconomyHook {

    val economy = paper.servicesManager.getRegistration(Economy::class.java)?.provider

}