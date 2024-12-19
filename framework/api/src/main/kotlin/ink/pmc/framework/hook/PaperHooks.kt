package ink.pmc.framework.hook

import ink.pmc.framework.platform.paper

var vaultHook: VaultHook? = null

fun initPaperHooks() {
    if (paper.pluginManager.getPlugin("Vault") != null) {
        vaultHook = VaultHook()
    }
}