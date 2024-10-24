package ink.pmc.utils.hook

import ink.pmc.utils.platform.paper

var economyHook: EconomyHook? = null

fun initPaperHooks() {
    if (paper.pluginManager.getPlugin("Vault") != null) {
        economyHook = EconomyHook()
    }
}