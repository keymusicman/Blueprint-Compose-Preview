package uk.co.gusward.blueprint.compose.preview.constants

import org.junit.Assert.assertEquals
import org.junit.Test

class DirectionUnitTest {

    @Test
    fun `test Direction enum values`() {
        assertEquals("Left", Direction.Left.name)
        assertEquals("Top", Direction.Top.name)
        assertEquals("Right", Direction.Right.name)
        assertEquals("Bottom", Direction.Bottom.name)
    }
}
