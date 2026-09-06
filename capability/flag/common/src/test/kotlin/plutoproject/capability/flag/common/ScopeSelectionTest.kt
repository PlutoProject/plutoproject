package plutoproject.capability.flag.common

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ScopeSelectionTest {
    @Test
    fun `empty input resolves to none`() {
        assertEquals(ScopeSelection.None, parseScopeSelection(null, allowWorld = true))
        assertEquals(ScopeSelection.None, parseScopeSelection("  ", allowWorld = true))
    }

    @Test
    fun `bare category flags parse without targets`() {
        assertEquals(ScopeSelection.Player(null), parseScopeSelection("--player", allowWorld = true))
        assertEquals(ScopeSelection.World(null), parseScopeSelection("--world", allowWorld = true))
    }

    @Test
    fun `value flags consume the following token`() {
        assertEquals(
            ScopeSelection.Player("some-uuid"),
            parseScopeSelection("--player some-uuid", allowWorld = true),
        )
        assertEquals(
            ScopeSelection.World("world_nether"),
            parseScopeSelection("--world world_nether", allowWorld = true),
        )
    }

    @Test
    fun `presence flags parse`() {
        assertEquals(ScopeSelection.Global, parseScopeSelection("--global", allowWorld = true))
        assertEquals(ScopeSelection.Server, parseScopeSelection("--server", allowWorld = true))
    }

    @Test
    fun `multiple scope flags conflict`() {
        assertEquals(
            ScopeSelection.Conflict,
            parseScopeSelection("--global --server", allowWorld = true),
        )
        assertEquals(
            ScopeSelection.Conflict,
            parseScopeSelection("--player a --player b", allowWorld = true),
        )
    }

    @Test
    fun `unknown tokens are invalid`() {
        assertEquals(ScopeSelection.Invalid, parseScopeSelection("--nope", allowWorld = true))
        assertEquals(ScopeSelection.Invalid, parseScopeSelection("stray token", allowWorld = true))
    }

    @Test
    fun `world flag is invalid when worlds are not allowed`() {
        assertEquals(ScopeSelection.Invalid, parseScopeSelection("--world", allowWorld = false))
        assertEquals(ScopeSelection.Invalid, parseScopeSelection("--world nether", allowWorld = false))
    }
}
