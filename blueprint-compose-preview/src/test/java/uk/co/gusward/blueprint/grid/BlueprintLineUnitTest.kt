package uk.co.gusward.blueprint.grid

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import uk.co.gusward.bluprint.grid.BlueprintLine
import uk.co.gusward.bluprint.items.BlueprintItemData
import uk.co.gusward.bluprint.items.WherePossible

class BlueprintLineUnitTest {

    @Test
    fun `test equality`() {
        val line1 = BlueprintLine(Offset(0f, 0f), Offset(10f, 10f))
        val line2 = BlueprintLine(Offset(0f, 0f), Offset(10f, 10f))
        val line3 = BlueprintLine(Offset(10f, 10f), Offset(0f, 0f))
        val line4 = BlueprintLine(Offset(0f, 0f), Offset(10f, 0f))

        assertEquals(line1, line2)
        assertEquals(line1, line3) // Reversed should be equal
        assertNotEquals(line1, line4)
    }

    @Test
    fun `test hashCode`() {
        val line1 = BlueprintLine(Offset(0f, 0f), Offset(10f, 10f))
        val line2 = BlueprintLine(Offset(0f, 0f), Offset(10f, 10f))
        
        assertEquals(line1.hashCode(), line2.hashCode())
    }

    @Test
    fun `test length`() {
        val horizontal = BlueprintLine(Offset(0f, 0f), Offset(10f, 0f))
        assertEquals(10f, horizontal.length, 0.001f)

        val vertical = BlueprintLine(Offset(0f, 0f), Offset(0f, 10f))
        assertEquals(10f, vertical.length, 0.001f)

        val diagonal = BlueprintLine(Offset(0f, 0f), Offset(3f, 4f))
        assertEquals(5f, diagonal.length, 0.001f)
    }

    @Test
    fun `test orientation`() {
        val horizontal = BlueprintLine(Offset(0f, 0f), Offset(10f, 0f))
        assertTrue(horizontal.isHorizontal)
        assertFalse(horizontal.isVertical)

        val vertical = BlueprintLine(Offset(0f, 0f), Offset(0f, 10f))
        assertFalse(vertical.isHorizontal)
        assertTrue(vertical.isVertical)

        val diagonal = BlueprintLine(Offset(0f, 0f), Offset(10f, 10f))
        assertFalse(diagonal.isHorizontal)
        assertFalse(diagonal.isVertical)
    }

    @Test
    fun `test midPoint`() {
        val line = BlueprintLine(Offset(0f, 0f), Offset(10f, 20f))
        assertEquals(Offset(5f, 10f), line.midPoint)
    }

    @Test
    fun `test intersects other line`() {
        val line1 = BlueprintLine(Offset(0f, 5f), Offset(10f, 5f))
        val line2 = BlueprintLine(Offset(5f, 0f), Offset(5f, 10f))
        assertTrue(line1.intersects(line2))

        val line3 = BlueprintLine(Offset(0f, 0f), Offset(10f, 0f))
        val line4 = BlueprintLine(Offset(0f, 1f), Offset(10f, 10f))
        assertFalse(line3.intersects(line4))

        // Parallel lines
        val line5 = BlueprintLine(Offset(0f, 0f), Offset(10f, 0f))
        val line6 = BlueprintLine(Offset(0f, 1f), Offset(10f, 1f))
        assertFalse(line5.intersects(line6))
    }

    @Test
    fun `test intersects item`() {
        val item = BlueprintItemData(
            id = "1",
            label = "Item",
            position = Offset(2f, 2f),
            size = Size(4f, 4f),
            parentConnectionConfig = WherePossible
        )

        // Line completely inside
        assertTrue(BlueprintLine(Offset(3f, 3f), Offset(5f, 5f)).intersects(item))

        // Line passing through
        assertTrue(BlueprintLine(Offset(0f, 4f), Offset(10f, 4f)).intersects(item))

        // Line ending inside
        assertTrue(BlueprintLine(Offset(0f, 0f), Offset(3f, 3f)).intersects(item))

        // Line outside
        assertFalse(BlueprintLine(Offset(0f, 0f), Offset(1f, 1f)).intersects(item))
        assertFalse(BlueprintLine(Offset(7f, 7f), Offset(10f, 10f)).intersects(item))
    }
}
