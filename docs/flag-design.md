# Flag System Design

A runtime business-parameter system: `key -> value` pairs that can be changed at runtime
without editing config files or restarting the server. Typical use cases: gray-release
toggles for features, per-player gray-release flags.

Flags are runtime-only and never persisted to MongoDB. They live in Redis.

## Capabilities

Two new capabilities, both following the standard `api / common / paper / velocity`
module layout:

1. **`redis`** — provides a shared `RedisConnection` service (Lettuce client). Built like
   `mongo`: `api` exposes the interface, `common` holds the `RuntimeModule` with a HOCON
   `config.conf` (`redisUri`, default `redis://localhost:6379`) and exports the service
   via Koin. Coroutine access goes through `StatefulRedisConnection.coroutines()`
   (`RedisCoroutinesCommands`, experimental API, requires opt-in and
   `kotlinx-coroutines-reactive`).

2. **`flag`** — the flag registry itself. Depends on the `redis` and `server-identifier`
   capabilities.

## Storage

All flags live in one Redis shared by the whole network (Velocity + all Paper servers).
Each scope is a single Redis hash; flag keys are hash fields:

```
plutoproject.flag.global                     -> hash { flagKey: value }
plutoproject.flag.server.<serverIdentifier>  -> hash { flagKey: value }
plutoproject.flag.world.<serverId>.<world>   -> hash { flagKey: value }
plutoproject.flag.player.<uuid>              -> hash { flagKey: value }
```

- `serverIdentifier` comes from the `server-identifier` capability. If it is configured
  as the reserved `_global`, the flag capability fails to load at startup.
- Two servers sharing an identifier is out of scope.
- Values are stored as strings; types are enforced at the API/command layer.

## Scope

```kotlin
sealed interface Scope {
    data object GLOBAL : Scope
    data object SERVER : Scope                 // fixed to the local server identifier
    data class Player(val uuid: UUID) : Scope
    data class World(val worldName: String) : Scope  // serverId filled in internally
}
```

- Factory functions in `api`: `Scope.player(uuid)`, `Scope.world(worldName)`,
  `Scope.SERVER`, `Scope.GLOBAL`. Paper adds extensions taking a `Player` object.
- On Velocity, `Scope.world(...)` throws `UnsupportedOperationException`.
- Fallback order: player -> world -> server -> global, starting from the scope passed to
  the API (or from `SERVER` when no scope is passed).

## Values

Supported types: `Boolean`, `Int`, `Long`, `Double`, `String`. Consumers register keys
with name, type, default value, and optional description. Only registered keys can be
set; reads return strongly typed values (unregistered keys return the default / null).

## API shape

```kotlin
interface FlagRegistry {
    fun register(key: FlagKey<*>)
    fun unregister(key: FlagKey<*>)

    // Sync, backed by a local cache (TTL configurable, default 5s).
    // scope = null falls back starting from SERVER.
    fun <T : Any> get(key: FlagKey<T>, scope: Scope? = null): T

    // Suspending, bypasses the cache.
    suspend fun <T : Any> getDirect(key: FlagKey<T>, scope: Scope? = null): T

    // Suspending writes; refresh the local cache of the executing node immediately.
    // Other nodes pick up changes when their TTL expires (no pub/sub in v1).
    suspend fun set(value: FlagValue<*>, scope: Scope)
    suspend fun clear(key: FlagKey<*>, scope: Scope)

    fun list(filter: ListFilter = ListFilter.All): FlagListing
}

sealed interface ListFilter {
    data object All : ListFilter                          // no flag
    data class Player(val uuid: UUID?) : ListFilter       // null = bare --player
    data class World(val worldName: String?) : ListFilter // null = bare --world
    data object Server : ListFilter
    data object Global : ListFilter
}
```

`FlagListing` is sealed with three subtypes matching the three list semantics:

- **With a target** (`Player(uuid)`, `World(name)`, `Server`, `Global`): effective value
  per registered key, resolved through the fallback chain, plus the scope that actually
  hit (`Effective`).
- **Bare category** (`Player(null)`, `World(null)`): raw entries set at any scope of that
  category, including the concrete target (which player / world), no fallback
  (`RawEntries`).
- **No flag** (`All`): every registered key with the scopes it is set at, including
  values (`Overview`).

`/flag get` is the single-key version of the "with a target" listing: it prints the value
and the scope that hit it.

## Commands

- Paper: `/flag`. Velocity: `/flagv` (same structure, no world subcommand).
- Permission: single node `plutoproject.flag.command.flag` on both platforms.
- Subcommands:
  ```
  /flag set global <key> <value>
  /flag set server <key> <value>
  /flag set world <world> <key> <value>
  /flag set player <player> <key> <value>
  /flag clear global|server|player <player>|world <world> <key>
  /flag get <key> [--global|--server|--world <w>|--player <p>]
  /flag list [--global|--server|--world [w]|--player [p]]
  ```
- `<player>` resolves to an online player name first (tab-completed), otherwise parsed as
  a raw UUID (offline players supported). No name-to-UUID database lookup.
- `get` without flags uses the API default (fallback from server). `get` prints the value
  and the scope that hit it.
- `list` without flags lists all registered keys with their per-scope settings.
- All player-facing text lives in `Messages.kt` files.
