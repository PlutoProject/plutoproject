package ink.pmc.framework.player

import ink.pmc.advkt.component.newline
import ink.pmc.advkt.component.text
import ink.pmc.advkt.send
import ink.pmc.framework.frameworkLogger
import ink.pmc.framework.chat.mochaMaroon
import ink.pmc.framework.chat.mochaSubtext0
import net.kyori.adventure.audience.Audience
import java.util.logging.Level

inline fun <T> T.catchException(
    audience: Audience? = null,
    onFailure: Audience.(Throwable) -> Unit = {},
    failureLog: String = "Exception caught",
    action: T.() -> Unit
) {
    runCatching {
        action()
    }.onFailure {
        audience?.onFailure(it)
        frameworkLogger.log(Level.SEVERE, failureLog, it)
    }
}

inline fun <T> T.catchExceptionInteraction(audience: Audience? = null, action: T.() -> Unit) {
    catchException(
        audience = audience,
        onFailure = {
            send {
                text("处理交互时出现服务器内部错误") with mochaMaroon
                newline()
                text("请将其反馈给管理组以便我们尽快解决") with mochaSubtext0
            }
        },
        failureLog = "Exception caught while handling interaction"
    ) {
        action()
    }
}