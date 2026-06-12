package uk.co.gusward.blueprint.compose.preview.items

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import uk.co.gusward.blueprint.compose.preview.constants.Direction

class ParentConnectionConfigUnitTest {

    @Test
    fun `test WherePossible`() {
        val config = WherePossible
        assertTrue(config.shouldConnectParent(Direction.Left))
        assertTrue(config.shouldConnectParent(Direction.Top))
        assertTrue(config.shouldConnectParent(Direction.Right))
        assertTrue(config.shouldConnectParent(Direction.Bottom))
    }

    @Test
    fun `test None`() {
        val config = None
        assertFalse(config.shouldConnectParent(Direction.Left))
        assertFalse(config.shouldConnectParent(Direction.Top))
        assertFalse(config.shouldConnectParent(Direction.Right))
        assertFalse(config.shouldConnectParent(Direction.Bottom))
    }

    @Test
    fun `test Specific`() {
        val config = Specific(
            left = WherePossible,
            top = None,
            right = WherePossible,
            bottom = None
        )
        assertTrue(config.shouldConnectParent(Direction.Left))
        assertFalse(config.shouldConnectParent(Direction.Top))
        assertTrue(config.shouldConnectParent(Direction.Right))
        assertFalse(config.shouldConnectParent(Direction.Bottom))
    }

    @Test
    fun `test Specific defaults`() {
        val config = Specific()
        assertTrue(config.shouldConnectParent(Direction.Left))
        assertTrue(config.shouldConnectParent(Direction.Top))
        assertTrue(config.shouldConnectParent(Direction.Right))
        assertTrue(config.shouldConnectParent(Direction.Bottom))
    }
}
