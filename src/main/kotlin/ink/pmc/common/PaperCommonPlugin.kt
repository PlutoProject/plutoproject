package ink.pmc.common

import org.bukkit.plugin.java.JavaPlugin

// 用于解决 runTask 启动服务器时会将项目根项目视为插件进行加载而导致报错的问题
// 同时作为是否开启调试模式的标识符

@Suppress("UNUSED")
class PaperCommonPlugin : JavaPlugin()