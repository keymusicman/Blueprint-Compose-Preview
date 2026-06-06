package uk.co.gusward.bluprint.grid.logic

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import org.junit.Assert.assertTrue
import org.junit.Test
import uk.co.gusward.bluprint.items.BlueprintItemData
import uk.co.gusward.bluprint.items.WherePossible

class BlueprintLineCalculatorUnitTest {

    @Test
    fun `test vertical connection between two items`() {
        val item1 = BlueprintItemData(
            id = "1",
            label = "Item 1",
            position = Offset(100f, 100f),
            size = Size(100f, 100f),
            parentConnectionConfig = WherePossible
        )
        val item2 = BlueprintItemData(
            id = "2",
            label = "Item 2",
            position = Offset(100f, 300f),
            size = Size(100f, 100f),
            parentConnectionConfig = WherePossible
        )
        
        val items = mapOf("1" to item1, "2" to item2)
        val screenSize = Size(1000f, 1000f)
        
        val lines = calculateBlueprintLines(items, screenSize)
        
        // Should have a line between item 1 and item 2
        // And lines to parent if WherePossible (but wait, parent connections are only if no items are in the way)
        
        val connectingLine = lines.find { it.isVertical && it.start.x == 150f && 
            (it.start.y == 200f && it.end.y == 300f || it.start.y == 300f && it.end.y == 200f) }
        
        assertTrue("Should find a connecting line between item 1 and 2", connectingLine != null)
    }

    @Test
    fun `test horizontal connection between two items`() {
        val item1 = BlueprintItemData(
            id = "1",
            label = "Item 1",
            position = Offset(100f, 100f),
            size = Size(100f, 100f),
            parentConnectionConfig = WherePossible
        )
        val item2 = BlueprintItemData(
            id = "2",
            label = "Item 2",
            position = Offset(300f, 100f),
            size = Size(100f, 100f),
            parentConnectionConfig = WherePossible
        )
        
        val items = mapOf("1" to item1, "2" to item2)
        val screenSize = Size(1000f, 1000f)
        
        val lines = calculateBlueprintLines(items, screenSize)
        
        val connectingLine = lines.find { it.isHorizontal && it.start.y == 150f && 
            (it.start.x == 200f && it.end.x == 300f || it.start.x == 300f && it.end.x == 200f) }
        
        assertTrue("Should find a connecting line between item 1 and 2", connectingLine != null)
    }

    @Test
    fun `test parent connections when no items in the way`() {
        val item = BlueprintItemData(
            id = "1",
            label = "Item 1",
            position = Offset(100f, 100f),
            size = Size(100f, 100f),
            parentConnectionConfig = WherePossible
        )
        
        val items = mapOf("1" to item)
        val screenSize = Size(500f, 500f)
        
        val lines = calculateBlueprintLines(items, screenSize)
        
        // Should have 4 lines to parent edges
        assertTrue("Should have 4 lines", lines.size == 4)
        
        assertTrue(lines.any { it.isHorizontal && it.start.y == 150f && (it.start.x == 100f && it.end.x == 0f || it.start.x == 0f && it.end.x == 100f) }) // Left
        assertTrue(lines.any { it.isVertical && it.start.x == 150f && (it.start.y == 100f && it.end.y == 0f || it.start.y == 0f && it.end.y == 100f) }) // Top
        assertTrue(lines.any { it.isHorizontal && it.start.y == 150f && (it.start.x == 200f && it.end.x == 500f || it.start.x == 500f && it.end.x == 200f) }) // Right
        assertTrue(lines.any { it.isVertical && it.start.x == 150f && (it.start.y == 200f && it.end.y == 500f || it.start.y == 500f && it.end.y == 200f) }) // Bottom
    }

    @Test
    fun `test lines do not intersect other items`() {
        val item1 = BlueprintItemData(
            id = "1",
            label = "Item 1",
            position = Offset(100f, 100f),
            size = Size(100f, 100f),
            parentConnectionConfig = WherePossible
        )
        val item2 = BlueprintItemData( // This one is in the way for horizontal connection from 1 to parent right
            id = "2",
            label = "Item 2",
            position = Offset(300f, 50f),
            size = Size(100f, 200f),
            parentConnectionConfig = WherePossible
        )
        
        val items = mapOf("1" to item1, "2" to item2)
        val screenSize = Size(1000f, 1000f)
        
        val lines = calculateBlueprintLines(items, screenSize)
        
        // Horizontal line from Item 1 to parent right should NOT exist because it intersects Item 2
        val lineToParentRight = lines.find { it.isHorizontal && it.start.y == 150f && it.start.x == 200f && it.end.x == 1000f }
        assertTrue("Line to parent right should not exist", lineToParentRight == null)
    }
}
