package ink.pmc.framework.utils.hook

import ink.pmc.framework.utils.platform.paper

var economyHook: EconomyHook? = null

fun initPaperHooks() {
    if (paper.pluginManager.getPlugin("Vault") != null) {
        economyHook = EconomyHook()
    }
}