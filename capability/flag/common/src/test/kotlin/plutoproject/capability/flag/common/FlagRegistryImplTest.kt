package plutoproject.capability.flag.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import plutoproject.capability.flag.api.FlagListing
import plutoproject.capability.flag.api.ListFilter
import plutoproject.capability.flag.api.Scope
import plutoproject.capability.flag.api.booleanFlag
import plutoproject.capability.flag.api.intFlag
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration.Companion.seconds

private val PLAYER_UUID = UUID.fromString("00000000-0000-0000-0000-000000000001")
private val SERVER_ID = "survival-1"

private val GRAYSCALE = booleanFlag("home.grayscale", default = false)
private val TIMEOUT = intFlag("home.grayscale.teleport_timeout", default = 10)

private class InMemoryFlagStore : FlagStore {
    val hashes = ConcurrentHashMap<String, ConcurrentHashMap<String, String>>()

    override suspend fun hashValues(scopeHash: String): Map<String, String> =
        hashes[scopeHash]?.toMap() ?: emptyMap()

    override suspend fun hashValue(scopeHash: String, field: String): String? =
        hashes[scopeHash]?.get(field)

    override suspend fun setValue(scopeHash: String, field: String, value: String) {
        hashes.getOrPut(scopeHash) { ConcurrentHashMap() }[field] = value
    }

    override suspend fun clearValue(scopeHash: String, field: String): Boolean {
        val removed = hashes[scopeHash]?.remove(field) != null
        if (removed && hashes[scopeHash]?.isEmpty() == true) {
            hashes.remove(scopeHash)
        }
        return removed
    }

    override suspend fun scanScopeHashes(match: String): List<String> {
        val prefix = match.removeSuffix("*")
        return hashes.keys.filter { it.startsWith(prefix) }.sorted()
    }
}

class FlagRegistryImplTest {
    private val store = InMemoryFlagStore()
    private val cache = FlagCache(5.seconds)
    private val backgroundScope = CoroutineScope(Dispatchers.Unconfined)
    private val registry = FlagRegistryImpl(
        store = store,
        serverIdentifier = SERVER_ID,
        cache = cache,
        backgroundScope = backgroundScope,
        allowWorldScope = true,
    )

    private suspend fun prime() {
        registry.register(GRAYSCALE)
        registry.register(TIMEOUT)
        registry.preload()
    }

    @Test
    fun `scopeless read falls back to server then global`() = runTest {
        prime()
        registry.set(GRAYSCALE, true, Scope.SERVER)

        assertEquals(true, registry.get(GRAYSCALE))
        assertEquals(false, registry.get(GRAYSCALE, Scope.GLOBAL))
    }

    @Test
    fun `player scope falls back to server without a world layer`() = runTest {
        prime()
        registry.set(GRAYSCALE, true, Scope.SERVER)

        assertEquals(true, registry.get(GRAYSCALE, Scope.Player(PLAYER_UUID)))
    }

    @Test
    fun `direct reads reflect store changes immediately`() = runTest {
        prime()
        assertEquals(false, registry.getDirect(GRAYSCALE))

        registry.set(GRAYSCALE, true, Scope.SERVER)
        assertEquals(true, registry.getDirect(GRAYSCALE))
    }

    @Test
    fun `cached reads ignore store changes until the entry expires`() = runTest {
        prime()
        assertEquals(false, registry.get(GRAYSCALE))

        store.setValue(serverScopeHash(SERVER_ID), GRAYSCALE.name, "true")
        assertEquals(false, registry.get(GRAYSCALE))
    }

    @Test
    fun `invalid stored values fall through to the next scope`() = runTest {
        prime()
        store.setValue(playerScopeHash(PLAYER_UUID), GRAYSCALE.name, "not-a-boolean")
        store.setValue(serverScopeHash(SERVER_ID), GRAYSCALE.name, "true")

        assertEquals(true, registry.getDirect(GRAYSCALE, Scope.Player(PLAYER_UUID)))
    }

    @Test
    fun `clear reports whether a value was removed`() = runTest {
        prime()
        registry.set(GRAYSCALE, true, Scope.SERVER)

        assertEquals(true, registry.clear(GRAYSCALE, Scope.SERVER))
        assertEquals(false, registry.clear(GRAYSCALE, Scope.SERVER))
        assertEquals(false, registry.get(GRAYSCALE))
    }

    @Test
    fun `world scopes are rejected when the platform does not support them`() = runTest {
        val velocityRegistry = FlagRegistryImpl(
            store = store,
            serverIdentifier = SERVER_ID,
            cache = cache,
            backgroundScope = backgroundScope,
            allowWorldScope = false,
        )
        velocityRegistry.register(GRAYSCALE)

        assertThrows(IllegalStateException::class.java) {
            runBlocking { velocityRegistry.get(GRAYSCALE, Scope.world("world")) }
        }
        assertThrows(IllegalStateException::class.java) {
            runBlocking { velocityRegistry.list(ListFilter.World(null)) }
        }
    }

    @Test
    fun `overview lists registered keys with all their settings`() = runTest {
        prime()
        registry.set(GRAYSCALE, true, Scope.SERVER)
        registry.set(GRAYSCALE, true, Scope.Player(PLAYER_UUID))
        registry.set(TIMEOUT, 5, Scope.GLOBAL)

        val listing = registry.list() as FlagListing.Overview
        val grayscale = listing.entries.first { it.key.name == GRAYSCALE.name }
        assertEquals(
            listOf(Scope.Player(PLAYER_UUID), Scope.Server(SERVER_ID)),
            grayscale.settings.map { it.scope },
        )
        val timeout = listing.entries.first { it.key.name == TIMEOUT.name }
        assertEquals(listOf(Scope.GLOBAL), timeout.settings.map { it.scope })
    }

    @Test
    fun `effective listing resolves through the fallback chain and reports the hit scope`() = runTest {
        prime()
        registry.set(GRAYSCALE, true, Scope.GLOBAL)
        registry.set(TIMEOUT, 15, Scope.SERVER)

        val listing = registry.list(ListFilter.Player(PLAYER_UUID)) as FlagListing.Effective
        val grayscale = listing.entries.first { it.key.name == GRAYSCALE.name }
        assertEquals("true", grayscale.value)
        assertEquals(Scope.GLOBAL, grayscale.hitScope)
        val timeout = listing.entries.first { it.key.name == TIMEOUT.name }
        assertEquals("15", timeout.value)
        assertEquals(Scope.SERVER, timeout.hitScope)
    }

    @Test
    fun `raw player listing shows every player scoped entry without fallback`() = runTest {
        prime()
        registry.set(GRAYSCALE, true, Scope.Player(PLAYER_UUID))
        registry.set(TIMEOUT, 15, Scope.SERVER)

        val listing = registry.list(ListFilter.Player(null)) as FlagListing.RawEntries
        assertEquals(1, listing.entries.size)
        val entry = listing.entries.single()
        assertEquals(GRAYSCALE.name, entry.key.name)
        assertEquals(Scope.Player(PLAYER_UUID), entry.scope)
        assertEquals("true", entry.value)
    }
}
