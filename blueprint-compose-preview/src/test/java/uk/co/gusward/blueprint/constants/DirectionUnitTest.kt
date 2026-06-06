package uk.co.gusward.blueprint.constants

import org.junit.Assert.assertEquals
import org.junit.Test
import uk.co.gusward.bluprint.constants.Direction

class DirectionUnitTest {

    @Test
    fun `test Direction enum values`() {
        assertEquals("Left", Direction.Left.name)
        assertEquals("Top", Direction.Top.name)
        assertEquals("Right", Direction.Right.name)
        assertEquals("Bottom", Direction.Bottom.name)
    }
}
