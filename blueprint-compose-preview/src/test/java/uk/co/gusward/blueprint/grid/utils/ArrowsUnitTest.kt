package uk.co.gusward.blueprint.grid.utils

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test
import uk.co.gusward.bluprint.constants.Direction
import uk.co.gusward.bluprint.grid.utils.createArrowPath

class ArrowsUnitTest {

    @Test
    fun `test createArrowPath Top`() {
        val mockPath = mockk<Path>(relaxed = true)
        val tip = Offset(100f, 100f)
        val length = 100f
        
        // hypotenuseLength = min(100 * 0.2, 15) = 15
        // baseLength = min(100 * 0.15, 10) = 10
        
        createArrowPath(Direction.Top, tip, length, mockPath)
        
        verify {
            mockPath.moveTo(100f, 100f)
            mockPath.lineTo(110f, 115f) // tip.x + baseLength, tip.y + hypotenuseLength
            mockPath.lineTo(90f, 115f)  // tip.x - baseLength, tip.y + hypotenuseLength
            mockPath.lineTo(100f, 100f)
            mockPath.close()
        }
    }

    @Test
    fun `test createArrowPath Bottom`() {
        val mockPath = mockk<Path>(relaxed = true)
        val tip = Offset(100f, 100f)
        val length = 100f
        
        createArrowPath(Direction.Bottom, tip, length, mockPath)
        
        verify {
            mockPath.moveTo(100f, 100f)
            mockPath.lineTo(110f, 85f) // tip.x + baseLength, tip.y - hypotenuseLength
            mockPath.lineTo(90f, 85f)  // tip.x - baseLength, tip.y - hypotenuseLength
            mockPath.lineTo(100f, 100f)
            mockPath.close()
        }
    }

    @Test
    fun `test createArrowPath Left`() {
        val mockPath = mockk<Path>(relaxed = true)
        val tip = Offset(100f, 100f)
        val length = 100f
        
        createArrowPath(Direction.Left, tip, length, mockPath)
        
        verify {
            mockPath.moveTo(100f, 100f)
            mockPath.lineTo(115f, 90f) // tip.x + hypotenuseLength, tip.y - baseLength
            mockPath.lineTo(115f, 110f) // tip.x + hypotenuseLength, tip.y + baseLength
            mockPath.lineTo(100f, 100f)
            mockPath.close()
        }
    }

    @Test
    fun `test createArrowPath Right`() {
        val mockPath = mockk<Path>(relaxed = true)
        val tip = Offset(100f, 100f)
        val length = 100f
        
        createArrowPath(Direction.Right, tip, length, mockPath)
        
        verify {
            mockPath.moveTo(100f, 100f)
            mockPath.lineTo(85f, 90f) // tip.x - hypotenuseLength, tip.y - baseLength
            mockPath.lineTo(85f, 110f) // tip.x - hypotenuseLength, tip.y + baseLength
            mockPath.lineTo(100f, 100f)
            mockPath.close()
        }
    }
}
