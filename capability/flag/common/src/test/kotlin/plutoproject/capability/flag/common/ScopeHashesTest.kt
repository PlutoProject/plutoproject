package plutoproject.capability.flag.common

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import plutoproject.capability.flag.api.Scope
import java.util.UUID

private val SERVER_ID = "survival-1"
private val PLAYER_UUID = UUID.fromString("00000000-0000-0000-0000-000000000001")

class ScopeHashesTest {
    @Test
    fun `scope hashes roundtrip through scopeHashToScope`() {
        assertEquals(Scope.GLOBAL, scopeHashToScope(globalScopeHash()))
        assertEquals(Scope.Server(SERVER_ID), scopeHashToScope(serverScopeHash(SERVER_ID)))
        assertEquals(
            Scope.World("world_nether", SERVER_ID),
            scopeHashToScope(worldScopeHash(SERVER_ID, "world_nether")),
        )
        assertEquals(Scope.Player(PLAYER_UUID), scopeHashToScope(playerScopeHash(PLAYER_UUID)))
    }

    @Test
    fun `scopeHashToScope rejects malformed hashes`() {
        assertNull(scopeHashToScope("unrelated.key"))
        assertNull(scopeHashToScope("${FLAG_KEY_PREFIX}server."))
        assertNull(scopeHashToScope("${FLAG_KEY_PREFIX}world.${SERVER_ID}"))
        assertNull(scopeHashToScope("${FLAG_KEY_PREFIX}world..nether"))
        assertNull(scopeHashToScope("${FLAG_KEY_PREFIX}player.not-a-uuid"))
        assertNull(scopeHashToScope("${FLAG_KEY_PREFIX}something.else"))
    }

    @Test
    fun `validateScope accepts local scopes and rejects foreign ones`() {
        validateScope(Scope.GLOBAL, SERVER_ID, allowWorldScope = true)
        validateScope(Scope.SERVER, SERVER_ID, allowWorldScope = true)
        validateScope(Scope.Server(SERVER_ID), SERVER_ID, allowWorldScope = true)
        validateScope(Scope.World("world"), SERVER_ID, allowWorldScope = true)
        validateScope(Scope.World("world", SERVER_ID), SERVER_ID, allowWorldScope = true)
        validateScope(Scope.Player(PLAYER_UUID), SERVER_ID, allowWorldScope = true)

        assertThrows(IllegalArgumentException::class.java) {
            validateScope(Scope.Server("other"), SERVER_ID, allowWorldScope = true)
        }
        assertThrows(IllegalArgumentException::class.java) {
            validateScope(Scope.World("world", "other"), SERVER_ID, allowWorldScope = true)
        }
        assertThrows(IllegalStateException::class.java) {
            validateScope(Scope.World("world"), SERVER_ID, allowWorldScope = false)
        }
        assertTrue(true)
    }
}
