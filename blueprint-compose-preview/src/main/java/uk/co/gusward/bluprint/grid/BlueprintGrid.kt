package uk.co.gusward.bluprint.grid

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import uk.co.gusward.bluprint.constants.Direction
import uk.co.gusward.bluprint.grid.logic.calculateBlueprintLines
import uk.co.gusward.bluprint.grid.utils.createArrowPath
import uk.co.gusward.bluprint.grid.utils.drawBlueprintLineLabel
import uk.co.gusward.bluprint.items.BlueprintItemData
import kotlin.math.min

@Composable
fun BlueprintGrid(
    gridSize: Dp,
    blueprintItems: Map<String, BlueprintItemData>,
    alpha: Float = 1f,
    content: @Composable () -> Unit
) {

    val spacing = LocalDensity.current.run { gridSize.toPx() }
    var screenSize by remember {
        mutableStateOf(Size.Zero)
    }

    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .onGloballyPositioned { layoutCoordinates ->
                screenSize = Size(
                    width = layoutCoordinates.size.width.toFloat(),
                    height = layoutCoordinates.size.height.toFloat(),
                )
            }
    ) {
        /* draw grid */
        Canvas(
            modifier = Modifier.fillMaxSize(),
        ) {

            val path = Path()

            path.moveTo(size.width, 0f)
            path.lineTo(size.width, size.height)
            path.lineTo(0f, size.height)
            path.lineTo(0f, 0f)

            clipPath(
                path = path,
                clipOp = ClipOp.Intersect
            ) {

                var start = spacing
                val gridColor = Color.White.copy(alpha = alpha * 0.5f)

                do {
                    drawLine(
                        start = Offset(x = 0f, y = start),
                        end = Offset(x = size.width, y = start),
                        color = gridColor,
                        strokeWidth = 1f,
                    )
                    drawLine(
                        start = Offset(x = start, y = 0f),
                        end = Offset(x = start, y = size.height),
                        color = gridColor,
                        strokeWidth = 1f,
                    )
                    start += spacing
                } while (start < size.width + size.height)
            }
        }

        /* display content FIRST so measuring lines draw over top of it */
        content()

        val textMeasurer = rememberTextMeasurer()
        val backgroundColor = MaterialTheme.colorScheme.background

        val measuredLineWidth = 4f
        val minimumScale = 0.05f

        /* draw blueprint items measured lines */
        Canvas(
            modifier = Modifier.fillMaxSize(),
        ) {

            val blueprintLines = calculateBlueprintLines(blueprintItems, screenSize)

            blueprintLines.forEach { blueprintLine ->

                val width = min(measuredLineWidth, blueprintLine.length * minimumScale)

                drawLine(
                    start = blueprintLine.start,
                    end = blueprintLine.end,
                    color = Color.White.copy(alpha = alpha),
                    strokeWidth = width,
                )

                if (blueprintLine.isVertical) {
                    val isStartAboveEnd = blueprintLine.start.y < blueprintLine.end.y
                    
                    val upArrowPath = createArrowPath(
                        direction = Direction.Top,
                        tip = if (isStartAboveEnd) blueprintLine.start else blueprintLine.end,
                        lengthOfLine = blueprintLine.length,
                    )

                    drawOutline(
                        outline = Outline.Generic(upArrowPath),
                        color = Color.White.copy(alpha = alpha),
                    )

                    val downArrowPath = createArrowPath(
                        direction = Direction.Bottom,
                        tip = if (isStartAboveEnd) blueprintLine.end else blueprintLine.start,
                        lengthOfLine = blueprintLine.length,
                    )

                    drawOutline(
                        outline = Outline.Generic(downArrowPath),
                        color = Color.White.copy(alpha = alpha),
                    )
                } else if (blueprintLine.isHorizontal) {
                    val isStartLeftOfEnd = blueprintLine.start.x < blueprintLine.end.x
                    
                    val leftArrowPath = createArrowPath(
                        direction = Direction.Left,
                        tip = if (isStartLeftOfEnd) blueprintLine.start else blueprintLine.end,
                        blueprintLine.length,
                    )

                    drawOutline(
                        outline = Outline.Generic(leftArrowPath),
                        color = Color.White.copy(alpha = alpha),
                    )

                    val rightArrowPath = createArrowPath(
                        direction = Direction.Right,
                        tip = if (isStartLeftOfEnd) blueprintLine.end else blueprintLine.start,
                        blueprintLine.length,
                    )

                    drawOutline(
                        outline = Outline.Generic(rightArrowPath),
                        color = Color.White.copy(alpha = alpha),
                    )
                }

                drawBlueprintLineLabel(textMeasurer, blueprintLine, backgroundColor)
            }
        }
    }
}
