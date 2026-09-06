package plutoproject.capability.flag.paper

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.JoinConfiguration
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.incendo.cloud.annotation.specifier.Greedy
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.Permission
import plutoproject.capability.flag.api.EffectiveEntry
import plutoproject.capability.flag.api.FlagKey
import plutoproject.capability.flag.api.FlagListing
import plutoproject.capability.flag.api.FlagRegistry
import plutoproject.capability.flag.api.ListFilter
import plutoproject.capability.flag.api.OverviewEntry
import plutoproject.capability.flag.api.RawEntry
import plutoproject.capability.flag.api.Scope
import plutoproject.capability.flag.api.serializeDefault
import plutoproject.capability.flag.common.ScopeSelection
import plutoproject.capability.flag.common.displayText
import plutoproject.capability.flag.common.parseScopeSelection
import plutoproject.capability.flag.common.setFromSerialized
import plutoproject.foundation.common.text.mochaGreen
import plutoproject.foundation.common.text.mochaSubtext0
import plutoproject.foundation.common.text.mochaText
import plutoproject.foundation.common.text.replace
import plutoproject.kernel.api.koinGet
import java.util.UUID

internal fun resolvePlayerToken(token: String): UUID? =
    runCatching { UUID.fromString(token) }.getOrNull()
        ?: Bukkit.getServer().onlinePlayers
            .firstOrNull { it.name.equals(token, ignoreCase = true) }
            ?.uniqueId

@Suppress("UNUSED")
internal object PaperFlagCommand {
    private val registry get() = koinGet<FlagRegistry>()

    // ---- set ----

    @Command("flag set global <key> <value>")
    @Permission(PERMISSION_COMMAND_FLAG)
    suspend fun CommandSender.setGlobal(
        @Argument("key", parserName = "flag-key") key: FlagKey<*>,
        @Argument("value") @Greedy value: String,
    ) = set(Scope.GLOBAL, key, value)

    @Command("flag set server <key> <value>")
    @Permission(PERMISSION_COMMAND_FLAG)
    suspend fun CommandSender.setServer(
        @Argument("key", parserName = "flag-key") key: FlagKey<*>,
        @Argument("value") @Greedy value: String,
    ) = set(Scope.SERVER, key, value)

    @Command("flag set world <world> <key> <value>")
    @Permission(PERMISSION_COMMAND_FLAG)
    suspend fun CommandSender.setWorld(
        @Argument("world", suggestions = "flag-world") world: String,
        @Argument("key", parserName = "flag-key") key: FlagKey<*>,
        @Argument("value") @Greedy value: String,
    ) = set(Scope.world(world), key, value)

    @Command("flag set player <player> <key> <value>")
    @Permission(PERMISSION_COMMAND_FLAG)
    suspend fun CommandSender.setPlayer(
        @Argument("player", parserName = "flag-player") player: UUID,
        @Argument("key", parserName = "flag-key") key: FlagKey<*>,
        @Argument("value") @Greedy value: String,
    ) = set(Scope.player(player), key, value)

    // ---- clear ----

    @Command("flag clear global <key>")
    @Permission(PERMISSION_COMMAND_FLAG)
    suspend fun CommandSender.clearGlobal(
        @Argument("key", parserName = "flag-key") key: FlagKey<*>,
    ) = clear(Scope.GLOBAL, key)

    @Command("flag clear server <key>")
    @Permission(PERMISSION_COMMAND_FLAG)
    suspend fun CommandSender.clearServer(
        @Argument("key", parserName = "flag-key") key: FlagKey<*>,
    ) = clear(Scope.SERVER, key)

    @Command("flag clear world <world> <key>")
    @Permission(PERMISSION_COMMAND_FLAG)
    suspend fun CommandSender.clearWorld(
        @Argument("world", suggestions = "flag-world") world: String,
        @Argument("key", parserName = "flag-key") key: FlagKey<*>,
    ) = clear(Scope.world(world), key)

    @Command("flag clear player <player> <key>")
    @Permission(PERMISSION_COMMAND_FLAG)
    suspend fun CommandSender.clearPlayer(
        @Argument("player", parserName = "flag-player") player: UUID,
        @Argument("key", parserName = "flag-key") key: FlagKey<*>,
    ) = clear(Scope.player(player), key)

    // ---- get ----

    @Command("flag get <key> [flags]")
    @Permission(PERMISSION_COMMAND_FLAG)
    suspend fun CommandSender.get(
        @Argument("key", parserName = "flag-key") key: FlagKey<*>,
        @Argument("flags") @Greedy flags: String?,
    ) {
        val filter = when (val selection = parseScopeSelection(flags, allowWorld = true)) {
            ScopeSelection.None, ScopeSelection.Server -> ListFilter.Server
            ScopeSelection.Global -> ListFilter.Global
            is ScopeSelection.World -> {
                val worldName = selection.worldName
                if (worldName == null) {
                    sendMessage(COMMAND_FLAG_GET_REQUIRES_TARGET)
                    return
                }
                ListFilter.World(worldName)
            }
            is ScopeSelection.Player -> {
                val token = selection.token
                if (token == null) {
                    sendMessage(COMMAND_FLAG_GET_REQUIRES_TARGET)
                    return
                }
                val uuid = resolvePlayerToken(token)
                if (uuid == null) {
                    sendMessage(COMMAND_FLAG_PLAYER_NOT_FOUND.replace("<input>", token))
                    return
                }
                ListFilter.Player(uuid)
            }
            ScopeSelection.Conflict -> {
                sendMessage(COMMAND_FLAG_SCOPE_CONFLICT)
                return
            }
            ScopeSelection.Invalid -> {
                sendMessage(COMMAND_FLAG_INVALID_SCOPE_FLAG)
                return
            }
        }
        val listing = registry.list(filter) as FlagListing.Effective
        val entry = listing.entries.first { it.key.name == key.name }
        val hitScope = entry.hitScope
        if (hitScope == null) {
            sendMessage(
                COMMAND_FLAG_GET_DEFAULT
                    .replace("<key>", key.name)
                    .replace("<value>", entry.value)
            )
        } else {
            sendMessage(
                COMMAND_FLAG_GET_VALUE
                    .replace("<key>", key.name)
                    .replace("<value>", entry.value)
                    .replace("<scope>", hitScope.displayText())
            )
        }
    }

    // ---- list ----

    @Command("flag list [flags]")
    @Permission(PERMISSION_COMMAND_FLAG)
    suspend fun CommandSender.list(@Argument("flags") @Greedy flags: String?) {
        val filter = when (val selection = parseScopeSelection(flags, allowWorld = true)) {
            ScopeSelection.None -> ListFilter.All
            ScopeSelection.Global -> ListFilter.Global
            ScopeSelection.Server -> ListFilter.Server
            is ScopeSelection.World -> ListFilter.World(selection.worldName)
            is ScopeSelection.Player -> {
                val token = selection.token
                val uuid = token?.let { resolvePlayerToken(it) }
                if (token != null && uuid == null) {
                    sendMessage(COMMAND_FLAG_PLAYER_NOT_FOUND.replace("<input>", token))
                    return
                }
                ListFilter.Player(uuid)
            }
            ScopeSelection.Conflict -> {
                sendMessage(COMMAND_FLAG_SCOPE_CONFLICT)
                return
            }
            ScopeSelection.Invalid -> {
                sendMessage(COMMAND_FLAG_INVALID_SCOPE_FLAG)
                return
            }
        }
        val body = when (val listing = registry.list(filter)) {
            is FlagListing.Overview -> {
                if (listing.entries.isEmpty()) {
                    sendMessage(COMMAND_FLAG_LIST_EMPTY)
                    return
                }
                joinLines(listing.entries.map { renderOverviewEntry(it) })
            }
            is FlagListing.Effective -> joinLines(listing.entries.map { renderEffectiveEntry(it) })
            is FlagListing.RawEntries -> {
                if (listing.entries.isEmpty()) {
                    sendMessage(COMMAND_FLAG_LIST_EMPTY)
                    return
                }
                joinLines(listing.entries.map { renderRawEntry(it) })
            }
        }
        sendMessage(COMMAND_FLAG_LIST_HEADER.append(Component.newline()).append(body))
    }

    // ---- 渲染 ----

    private fun renderOverviewEntry(entry: OverviewEntry): Component {
        if (entry.settings.isEmpty()) {
            return Component.text()
                .append(Component.text(entry.key.name, mochaGreen))
                .append(Component.text(" = ${entry.key.serializeDefault()} ", mochaText))
                .append(Component.text("（默认）", mochaSubtext0))
                .build()
        }
        val builder = Component.text().append(Component.text(entry.key.name, mochaGreen))
        entry.settings.forEach { setting ->
            builder.append(Component.newline())
                .append(Component.text("  ${setting.scope.displayText()} = ", mochaSubtext0))
                .append(Component.text(setting.value, mochaText))
        }
        return builder.build()
    }

    private fun renderEffectiveEntry(entry: EffectiveEntry): Component {
        val suffix = entry.hitScope
            ?.displayText()
            ?.let { Component.text("（命中 $it）", mochaSubtext0) }
            ?: Component.text("（默认）", mochaSubtext0)
        return Component.text()
            .append(Component.text(entry.key.name, mochaGreen))
            .append(Component.text(" = ${entry.value} ", mochaText))
            .append(suffix)
            .build()
    }

    private fun renderRawEntry(entry: RawEntry): Component = Component.text()
        .append(Component.text(entry.key.name, mochaGreen))
        .append(Component.text(" = ${entry.value} ", mochaText))
        .append(Component.text("（${entry.scope.displayText()}）", mochaSubtext0))
        .build()

    private fun joinLines(lines: List<Component>): Component =
        Component.join(JoinConfiguration.separator(Component.newline()), lines)

    // ---- set/clear 公共逻辑 ----

    private suspend fun CommandSender.set(scope: Scope, key: FlagKey<*>, value: String) {
        if (!registry.setFromSerialized(key, value, scope)) {
            sendMessage(
                COMMAND_FLAG_INVALID_VALUE
                    .replace("<value>", value)
                    .replace("<type>", key.type.id)
            )
            return
        }
        sendMessage(
            COMMAND_FLAG_SET_SUCCEED
                .replace("<key>", key.name)
                .replace("<value>", value)
                .replace("<scope>", scope.displayText())
        )
    }

    private suspend fun CommandSender.clear(scope: Scope, key: FlagKey<*>) {
        val replaced = registry.clear(key, scope)
        val message = if (replaced) COMMAND_FLAG_CLEAR_SUCCEED else COMMAND_FLAG_CLEAR_NOTHING
        sendMessage(
            message
                .replace("<key>", key.name)
                .replace("<scope>", scope.displayText())
        )
    }
}
