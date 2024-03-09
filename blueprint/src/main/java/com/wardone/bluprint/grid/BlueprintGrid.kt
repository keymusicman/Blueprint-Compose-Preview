package com.wardone.bluprint.grid

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
import com.wardone.bluprint.constants.Direction
import com.wardone.bluprint.items.BlueprintItemData
import com.wardone.bluprint.grid.utils.createArrowPath
import com.wardone.bluprint.grid.utils.drawBlueprintLineLabel

@Composable
fun BlueprintGrid(
    gridSize: Dp,
    blueprintItems: Map<String, BlueprintItemData>,
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

                do {
                    drawLine(
                        start = Offset(x = 0f, y = start),
                        end = Offset(x = size.width, y = start),
                        color = Color.White,
                        strokeWidth = 1f,
                    )
                    drawLine(
                        start = Offset(x = start, y = 0f),
                        end = Offset(x = start, y = size.height),
                        color = Color.White,
                        strokeWidth = 1f,
                    )
                    start += spacing
                } while (start < size.width + size.height)
            }
        }

        val textMeasurer = rememberTextMeasurer()
        val backgroundColor = MaterialTheme.colorScheme.background

        /* draw blueprint items measured lines */
        Canvas(
            modifier = Modifier,
        ) {

            blueprintItems.forEach { currentEntry ->

                val completedBlueprintLines = mutableListOf<BlueprintLine>()

                /* draw VERTICAL connecting lines where there is direct line of sight */
                blueprintItems.values.filter { other ->
                    currentEntry.value != other && currentEntry.value isDirectlyAbove other
                }.forEach lineOfSightEntries@ { other ->

                    val blueprintLine = BlueprintLine(
                        start = Offset(
                            x = currentEntry.value.position.x + (currentEntry.value.size.width / 2),
                            y = currentEntry.value.position.y + currentEntry.value.size.height,
                        ),
                        end = Offset(
                            x = currentEntry.value.position.x + (currentEntry.value.size.width / 2),
                            y = other.position.y,
                        )
                    )

                    val clashesWithExistingLine = completedBlueprintLines.any {
                        it.clashes(blueprintLine)
                    }

                    if (clashesWithExistingLine) {
                        return@lineOfSightEntries
                    }

                    drawLine(
                        start = blueprintLine.start,
                        end = blueprintLine.end,
                        color = Color.White,
                        strokeWidth = 6f,
                    )

                    drawLine(
                        start = blueprintLine.start,
                        end = blueprintLine.end,
                        color = Color.White,
                        strokeWidth = 6f,
                    )

                    val upArrowPath = createArrowPath(
                        direction = Direction.Top,
                        tip = blueprintLine.start
                    )

                    drawOutline(
                        outline = Outline.Generic(upArrowPath),
                        color = Color.White,
                    )

                    val downArrowPath = createArrowPath(
                        direction = Direction.Bottom,
                        tip = blueprintLine.end
                    )

                    drawOutline(
                        outline = Outline.Generic(downArrowPath),
                        color = Color.White,
                    )

                    drawBlueprintLineLabel(textMeasurer, blueprintLine, backgroundColor)

                    completedBlueprintLines += blueprintLine
                }

                /* draw horizontal connecting lines where there is direct line of sight */
                blueprintItems.values.filter { other ->
                    currentEntry.value != other && currentEntry.value isDirectlyLeftOf other
                }.forEach lineOfSightEntries@ { other ->

                    val blueprintLine = BlueprintLine(
                        start = Offset(
                            x = currentEntry.value.position.x + currentEntry.value.size.width,
                            y = currentEntry.value.position.y + (currentEntry.value.size.height / 2),
                        ),
                        end = Offset(
                            x = other.position.x,
                            y = other.position.y + (currentEntry.value.size.height / 2),
                        )
                    )

                    val clashesWithExistingLine = completedBlueprintLines.any {
                        it.clashes(blueprintLine)
                    }

                    if (clashesWithExistingLine) {
                        return@lineOfSightEntries
                    }

                    drawLine(
                        start = blueprintLine.start,
                        end = blueprintLine.end,
                        color = Color.White,
                        strokeWidth = 6f,
                    )

                    val leftArrowPath = createArrowPath(
                        direction = Direction.Left,
                        tip = blueprintLine.start
                    )

                    drawOutline(
                        outline = Outline.Generic(leftArrowPath),
                        color = Color.White,
                    )

                    val rightArrowPath = createArrowPath(
                        direction = Direction.Right,
                        tip = blueprintLine.end
                    )

                    drawOutline(
                        outline = Outline.Generic(rightArrowPath),
                        color = Color.White,
                    )

                    drawBlueprintLineLabel(textMeasurer, blueprintLine, backgroundColor)

                    completedBlueprintLines += blueprintLine
                }

                /* draw direct lines of sight to parent */

                val parentDirectLeft = blueprintItems.values.none { other ->
                    currentEntry.value isDirectlyRightOf other
                } && currentEntry.value.parentConnectionConfig
                    .shouldConnectParent(Direction.Left)

                if (parentDirectLeft) {
                    val blueprintLine = BlueprintLine(
                        start = Offset(
                            x = currentEntry.value.position.x,
                            y = currentEntry.value.position.y + (currentEntry.value.size.height / 2),
                        ),
                        end = Offset(
                            x = 0f,
                            y = currentEntry.value.position.y + (currentEntry.value.size.height / 2),
                        )
                    )
                    drawLine(
                        start = blueprintLine.start,
                        end = blueprintLine.end,
                        color = Color.White,
                        strokeWidth = 6f,
                    )

                    drawLine(
                        start = blueprintLine.start,
                        end = blueprintLine.end,
                        color = Color.White,
                        strokeWidth = 6f,
                    )

                    val leftArrowPath = createArrowPath(
                        direction = Direction.Left,
                        tip = blueprintLine.end
                    )

                    drawOutline(
                        outline = Outline.Generic(leftArrowPath),
                        color = Color.White,
                    )

                    val rightArrowPath = createArrowPath(
                        direction = Direction.Right,
                        tip = blueprintLine.start
                    )

                    drawOutline(
                        outline = Outline.Generic(rightArrowPath),
                        color = Color.White,
                    )

                    drawBlueprintLineLabel(textMeasurer, blueprintLine, backgroundColor)
                }

                val parentDirectTop = blueprintItems.values.none { other ->
                    currentEntry.value isDirectlyBelow other
                } && currentEntry.value.parentConnectionConfig
                    .shouldConnectParent(Direction.Top)

                if (parentDirectTop) {
                    val blueprintLine = BlueprintLine(
                        start = Offset(
                            x = currentEntry.value.position.x + (currentEntry.value.size.width / 2),
                            y = currentEntry.value.position.y,
                        ),
                        end = Offset(
                            x = currentEntry.value.position.x + (currentEntry.value.size.width / 2),
                            y = 0f,
                        )
                    )

                    drawLine(
                        start = blueprintLine.start,
                        end = blueprintLine.end,
                        color = Color.White,
                        strokeWidth = 6f,
                    )

                    val upArrowPath = createArrowPath(
                        direction = Direction.Top,
                        tip = blueprintLine.end
                    )

                    drawOutline(
                        outline = Outline.Generic(upArrowPath),
                        color = Color.White,
                    )

                    val downArrowPath = createArrowPath(
                        direction = Direction.Bottom,
                        tip = blueprintLine.start
                    )

                    drawOutline(
                        outline = Outline.Generic(downArrowPath),
                        color = Color.White,
                    )

                    drawBlueprintLineLabel(textMeasurer, blueprintLine, backgroundColor)
                }

                val parentDirectRight = blueprintItems.values.none { other ->
                    currentEntry.value isDirectlyLeftOf other
                } && currentEntry.value.parentConnectionConfig
                    .shouldConnectParent(Direction.Right)

                if (parentDirectRight) {
                    val blueprintLine = BlueprintLine(
                        start = Offset(
                            x = currentEntry.value.position.x + currentEntry.value.size.width,
                            y = currentEntry.value.position.y + (currentEntry.value.size.height / 2),
                        ),
                        end = Offset(
                            x = screenSize.width,
                            y = currentEntry.value.position.y + (currentEntry.value.size.height / 2),
                        )
                    )

                    drawLine(
                        start = blueprintLine.start,
                        end = blueprintLine.end,
                        color = Color.White,
                        strokeWidth = 6f,
                    )

                    val leftArrowPath = createArrowPath(
                        direction = Direction.Left,
                        tip = blueprintLine.start
                    )

                    drawOutline(
                        outline = Outline.Generic(leftArrowPath),
                        color = Color.White,
                    )

                    val rightArrowPath = createArrowPath(
                        direction = Direction.Right,
                        tip = blueprintLine.end
                    )

                    drawOutline(
                        outline = Outline.Generic(rightArrowPath),
                        color = Color.White,
                    )

                    drawBlueprintLineLabel(textMeasurer, blueprintLine, backgroundColor)
                }

                val parentDirectBottom = blueprintItems.values.none { other ->
                    currentEntry.value isDirectlyAbove other
                } && currentEntry.value.parentConnectionConfig
                    .shouldConnectParent(Direction.Bottom)

                if (parentDirectBottom) {
                    val blueprintLine = BlueprintLine(
                        start = Offset(
                            x = currentEntry.value.position.x + currentEntry.value.size.width / 2,
                            y = currentEntry.value.position.y + currentEntry.value.size.height,
                        ),
                        end = Offset(
                            x = currentEntry.value.position.x + currentEntry.value.size.width / 2,
                            y = screenSize.height,
                        )
                    )

                    drawLine(
                        start = blueprintLine.start,
                        end = blueprintLine.end,
                        color = Color.White,
                        strokeWidth = 6f,
                    )

                    val upArrowPath = createArrowPath(
                        direction = Direction.Top,
                        tip = blueprintLine.start
                    )

                    drawOutline(
                        outline = Outline.Generic(upArrowPath),
                        color = Color.White,
                    )

                    val downArrowPath = createArrowPath(
                        direction = Direction.Top,
                        tip = blueprintLine.end
                    )

                    drawOutline(
                        outline = Outline.Generic(downArrowPath),
                        color = Color.White,
                    )

                    drawBlueprintLineLabel(textMeasurer, blueprintLine, backgroundColor)
                }
            }
        }

        /* display content */
        content()
    }
}
