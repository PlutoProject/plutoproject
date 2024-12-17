package ink.pmc.framework.utils.hook

import ink.pmc.framework.utils.platform.paper

var vaultHook: VaultHook? = null

fun initPaperHooks() {
    if (paper.pluginManager.getPlugin("Vault") != null) {
        vaultHook = VaultHook()
    }
}