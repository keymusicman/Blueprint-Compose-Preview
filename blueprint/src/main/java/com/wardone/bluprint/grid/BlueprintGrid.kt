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
import kotlin.math.min

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

            val validBlueprintItems = blueprintItems.filterValues { it.size.width > 0 && it.size.height > 0 }
            val completedBlueprintLines = mutableListOf<BlueprintLine>()
            val connectedHorizontalPairs = mutableSetOf<Set<String>>()
            val connectedVerticalPairs = mutableSetOf<Set<String>>()
            val lineSpacingOffset = 40f // Spacing between parallel lines

            validBlueprintItems.forEach { currentEntry ->

                /* draw VERTICAL connecting lines where there is direct line of sight */
                validBlueprintItems.values.filter { other ->
                    currentEntry.value != other && (currentEntry.value isDirectlyAbove other || currentEntry.value isDirectlyBelow other)
                }.forEach lineOfSightEntries@ { other ->

                    val isAbove = currentEntry.value isDirectlyAbove other

                    // 1. DEDUPLICATE BY ITEM PAIR: Only one vertical connection between two items
                    val itemPair = setOf(currentEntry.value.id, other.id)
                    if (connectedVerticalPairs.contains(itemPair)) return@lineOfSightEntries
                    connectedVerticalPairs.add(itemPair)

                    // Calculate the center of the shared X-axis overlap for better visual balance
                    val overlapStartX = maxOf(currentEntry.value.position.x, other.position.x)
                    val overlapEndX = minOf(currentEntry.value.position.x + currentEntry.value.size.width, other.position.x + other.size.width)
                    val baseVerticalLineX = (overlapStartX + overlapEndX) / 2
                    
                    // 2. DEDUPLICATE BY LINE: Check if the base connection already exists
                    val baseLine = BlueprintLine(
                        start = Offset(
                            x = baseVerticalLineX,
                            y = currentEntry.value.position.y + if (isAbove) currentEntry.value.size.height else 0f,
                        ),
                        end = Offset(
                            x = baseVerticalLineX,
                            y = other.position.y + if (isAbove) 0f else other.size.height,
                        )
                    )
                    if (completedBlueprintLines.contains(baseLine)) {
                        return@lineOfSightEntries
                    }

                    // 2. SHIFT SECOND: If it's a new line, check for spatial overlaps and offset
                    var verticalLineX = baseVerticalLineX
                    val overlaps = completedBlueprintLines.filter { 
                        it.isVertical && it.start.x == verticalLineX &&
                        maxOf(it.start.y, it.end.y) > minOf(currentEntry.value.position.y, other.position.y) &&
                        minOf(it.start.y, it.end.y) < maxOf(currentEntry.value.position.y, other.position.y)
                    }
                    
                    if (overlaps.isNotEmpty()) {
                        verticalLineX += (overlaps.size * lineSpacingOffset)
                    }

                    val blueprintLine = BlueprintLine(
                        start = Offset(
                            x = verticalLineX,
                            y = currentEntry.value.position.y
                                    + if (isAbove) currentEntry.value.size.height else 0f,
                        ),
                        end = Offset(
                            x = verticalLineX,
                            y = other.position.y
                                    + if (isAbove) 0f else other.size.height,
                        )
                    )

                    val intersectsAnyBlueprintItem = validBlueprintItems
                        .filter {
                            it.value != currentEntry.value && it.value != other
                        }
                        .any {
                            blueprintLine.intersects(it.value)
                        }

                    if (intersectsAnyBlueprintItem) {
                        return@lineOfSightEntries
                    }

                    val width = min(measuredLineWidth, blueprintLine.length * minimumScale)

                    drawLine(
                        start = blueprintLine.start,
                        end = blueprintLine.end,
                        color = Color.White,
                        strokeWidth = width,
                    )

                    val upArrowPath = createArrowPath(
                        direction = Direction.Top,
                        tip = if (isAbove) blueprintLine.start else blueprintLine.end,
                        lengthOfLine = blueprintLine.length,
                    )

                    drawOutline(
                        outline = Outline.Generic(upArrowPath),
                        color = Color.White,
                    )

                    val downArrowPath = createArrowPath(
                        direction = Direction.Bottom,
                        tip = if (isAbove) blueprintLine.end else blueprintLine.start,
                        lengthOfLine = blueprintLine.length,
                    )

                    drawOutline(
                        outline = Outline.Generic(downArrowPath),
                        color = Color.White,
                    )

                    drawBlueprintLineLabel(textMeasurer, blueprintLine, backgroundColor)

                    completedBlueprintLines += blueprintLine
                }

                /* draw horizontal connecting lines where there is direct line of sight */
                validBlueprintItems.values
                    .filter { other ->
                    currentEntry.value != other && (currentEntry.value isDirectlyLeftOf other || currentEntry.value isDirectlyRightOf other )
                }.forEach lineOfSightEntries@ { other ->

                    val isLeft = currentEntry.value isDirectlyLeftOf other

                    // 1. DEDUPLICATE BY ITEM PAIR
                    val itemPair = setOf(currentEntry.value.id, other.id)
                    if (connectedHorizontalPairs.contains(itemPair)) return@lineOfSightEntries
                    connectedHorizontalPairs.add(itemPair)

                    // Calculate the center of the shared Y-axis overlap for better visual balance
                    val overlapStartY = maxOf(currentEntry.value.position.y, other.position.y)
                    val overlapEndY = minOf(currentEntry.value.position.y + currentEntry.value.size.height, other.position.y + other.size.height)
                    val baseHorizontalLineY = (overlapStartY + overlapEndY) / 2

                    // 2. DEDUPLICATE BY LINE
                    val baseLine = BlueprintLine(
                        start = Offset(
                            x = currentEntry.value.position.x + if (isLeft) currentEntry.value.size.width else 0f,
                            y = baseHorizontalLineY,
                        ),
                        end = Offset(
                            x = other.position.x + if (isLeft) 0f else other.size.width,
                            y = baseHorizontalLineY,
                        )
                    )
                    if (completedBlueprintLines.contains(baseLine)) {
                        return@lineOfSightEntries
                    }

                    // 2. SHIFT SECOND
                    var horizontalLineY = baseHorizontalLineY
                    val overlaps = completedBlueprintLines.filter { 
                        it.isHorizontal && it.start.y == horizontalLineY &&
                        maxOf(it.start.x, it.end.x) > minOf(currentEntry.value.position.x, other.position.x) &&
                        minOf(it.start.x, it.end.x) < maxOf(currentEntry.value.position.x, other.position.x)
                    }
                    
                    if (overlaps.isNotEmpty()) {
                        horizontalLineY += (overlaps.size * lineSpacingOffset)
                    }

                    val blueprintLine = BlueprintLine(
                        start = Offset(
                            x = currentEntry.value.position.x
                                    + if (isLeft) currentEntry.value.size.width else 0f,
                            y = horizontalLineY,
                        ),
                        end = Offset(
                            x = other.position.x
                                    + if (isLeft) 0f else  + other.size.width,
                            y = horizontalLineY,
                        )
                    )

                    val intersectsAnyBlueprintItem = validBlueprintItems
                        .filter {
                            it.value != currentEntry.value && it.value != other
                        }
                        .any {
                            blueprintLine.intersects(it.value)
                        }

                    if (intersectsAnyBlueprintItem) {
                        return@lineOfSightEntries
                    }

                    val width = min(measuredLineWidth, blueprintLine.length * minimumScale)

                    drawLine(
                        start = blueprintLine.start,
                        end = blueprintLine.end,
                        color = Color.White,
                        strokeWidth = width,
                    )

                    val leftArrowPath = createArrowPath(
                        direction = Direction.Left,
                        tip = if (isLeft) blueprintLine.start else blueprintLine.end,
                        blueprintLine.length,
                    )

                    drawOutline(
                        outline = Outline.Generic(leftArrowPath),
                        color = Color.White,
                    )

                    val rightArrowPath = createArrowPath(
                        direction = Direction.Right,
                        tip = if (isLeft) blueprintLine.end else blueprintLine.start,
                        blueprintLine.length,
                    )

                    drawOutline(
                        outline = Outline.Generic(rightArrowPath),
                        color = Color.White,
                    )

                    drawBlueprintLineLabel(textMeasurer, blueprintLine, backgroundColor)

                    completedBlueprintLines += blueprintLine
                }

                /* draw direct lines of sight to parent */

                val parentDirectLeft = validBlueprintItems.values.none { other ->
                    currentEntry.value isDirectlyRightOf other
                } && currentEntry.value.parentConnectionConfig
                    .shouldConnectParent(Direction.Left)

                if (parentDirectLeft) {

                    var horizontalLineY = currentEntry.value.position.y + (currentEntry.value.size.height / 2)

                    // Check for overlaps with already drawn lines
                    val overlaps = completedBlueprintLines.filter { 
                        it.isHorizontal && it.start.y == horizontalLineY &&
                        maxOf(it.start.x, it.end.x) > minOf(0f, currentEntry.value.position.x) &&
                        minOf(it.start.x, it.end.x) < maxOf(0f, currentEntry.value.position.x)
                    }
                    
                    if (overlaps.isNotEmpty()) {
                        horizontalLineY += (overlaps.size * lineSpacingOffset)
                    }

                    val blueprintLine = BlueprintLine(
                        start = Offset(
                            x = currentEntry.value.position.x,
                            y = horizontalLineY,
                        ),
                        end = Offset(
                            x = 0f,
                            y = horizontalLineY,
                        )
                    )

                    val intersectsAnyBlueprintItem = validBlueprintItems
                        .filter { it.value != currentEntry.value }
                        .any { blueprintLine.intersects(it.value) }

                    if (!intersectsAnyBlueprintItem) {

                        val width = min(measuredLineWidth, blueprintLine.length * minimumScale)

                        drawLine(
                            start = blueprintLine.start,
                            end = blueprintLine.end,
                            color = Color.White,
                            strokeWidth = width,
                        )

                        val leftArrowPath = createArrowPath(
                            direction = Direction.Left,
                            tip = blueprintLine.end,
                            blueprintLine.length,
                        )

                        drawOutline(
                            outline = Outline.Generic(leftArrowPath),
                            color = Color.White,
                        )

                        val rightArrowPath = createArrowPath(
                            direction = Direction.Right,
                            tip = blueprintLine.start,
                            blueprintLine.length,
                        )

                        drawOutline(
                            outline = Outline.Generic(rightArrowPath),
                            color = Color.White,
                        )

                        drawBlueprintLineLabel(textMeasurer, blueprintLine, backgroundColor)

                        completedBlueprintLines += blueprintLine
                    }
                }

                val parentDirectTop = validBlueprintItems.values.none { other ->
                    currentEntry.value isDirectlyBelow other
                } && currentEntry.value.parentConnectionConfig
                    .shouldConnectParent(Direction.Top)

                if (parentDirectTop) {

                    var verticalLineX = currentEntry.value.position.x + (currentEntry.value.size.width / 2)

                    // Check for overlaps
                    val overlaps = completedBlueprintLines.filter { 
                        it.isVertical && it.start.x == verticalLineX &&
                        maxOf(it.start.y, it.end.y) > minOf(0f, currentEntry.value.position.y) &&
                        minOf(it.start.y, it.end.y) < maxOf(0f, currentEntry.value.position.y)
                    }
                    
                    if (overlaps.isNotEmpty()) {
                        verticalLineX += (overlaps.size * lineSpacingOffset)
                    }

                    val blueprintLine = BlueprintLine(
                        start = Offset(
                            x = verticalLineX,
                            y = currentEntry.value.position.y,
                        ),
                        end = Offset(
                            x = verticalLineX,
                            y = 0f,
                        )
                    )

                    val intersectsAnyBlueprintItem = validBlueprintItems
                        .filter { it.value != currentEntry.value }
                        .any { blueprintLine.intersects(it.value) }

                    if (!intersectsAnyBlueprintItem) {

                        val width = min(measuredLineWidth, blueprintLine.length * minimumScale)

                        drawLine(
                            start = blueprintLine.start,
                            end = blueprintLine.end,
                            color = Color.White,
                            strokeWidth = width,
                        )

                        val upArrowPath = createArrowPath(
                            direction = Direction.Top,
                            tip = blueprintLine.end,
                            blueprintLine.length,
                        )

                        drawOutline(
                            outline = Outline.Generic(upArrowPath),
                            color = Color.White,
                        )

                        val downArrowPath = createArrowPath(
                            direction = Direction.Bottom,
                            tip = blueprintLine.start,
                            blueprintLine.length,
                        )

                        drawOutline(
                            outline = Outline.Generic(downArrowPath),
                            color = Color.White,
                        )

                        drawBlueprintLineLabel(textMeasurer, blueprintLine, backgroundColor)

                        completedBlueprintLines += blueprintLine
                    }
                }

                val parentDirectRight = validBlueprintItems.values.none { other ->
                    currentEntry.value isDirectlyLeftOf other
                } && currentEntry.value.parentConnectionConfig
                    .shouldConnectParent(Direction.Right)

                if (parentDirectRight) {

                    var horizontalLineY = currentEntry.value.position.y + (currentEntry.value.size.height / 2)

                    // Check for overlaps
                    val overlaps = completedBlueprintLines.filter { 
                        it.isHorizontal && it.start.y == horizontalLineY &&
                        maxOf(it.start.x, it.end.x) > minOf(currentEntry.value.position.x + currentEntry.value.size.width, screenSize.width) &&
                        minOf(it.start.x, it.end.x) < maxOf(currentEntry.value.position.x + currentEntry.value.size.width, screenSize.width)
                    }
                    
                    if (overlaps.isNotEmpty()) {
                        horizontalLineY += (overlaps.size * lineSpacingOffset)
                    }

                    val blueprintLine = BlueprintLine(
                        start = Offset(
                            x = currentEntry.value.position.x + currentEntry.value.size.width,
                            y = horizontalLineY,
                        ),
                        end = Offset(
                            x = screenSize.width,
                            y = horizontalLineY,
                        )
                    )

                    val intersectsAnyBlueprintItem = validBlueprintItems
                        .filter { it.value != currentEntry.value }
                        .any { blueprintLine.intersects(it.value) }

                    if (!intersectsAnyBlueprintItem) {

                        val width = min(measuredLineWidth, blueprintLine.length * minimumScale)

                        drawLine(
                            start = blueprintLine.start,
                            end = blueprintLine.end,
                            color = Color.White,
                            strokeWidth = width,
                        )

                        val leftArrowPath = createArrowPath(
                            direction = Direction.Left,
                            tip = blueprintLine.start,
                            blueprintLine.length,
                        )

                        drawOutline(
                            outline = Outline.Generic(leftArrowPath),
                            color = Color.White,
                        )

                        val rightArrowPath = createArrowPath(
                            direction = Direction.Right,
                            tip = blueprintLine.end,
                            blueprintLine.length,
                        )

                        drawOutline(
                            outline = Outline.Generic(rightArrowPath),
                            color = Color.White,
                        )

                        drawBlueprintLineLabel(textMeasurer, blueprintLine, backgroundColor)

                        completedBlueprintLines += blueprintLine
                    }
                }

                val parentDirectBottom = validBlueprintItems.values.none { other ->
                    currentEntry.value isDirectlyAbove other
                } && currentEntry.value.parentConnectionConfig
                    .shouldConnectParent(Direction.Bottom)

                if (parentDirectBottom) {

                    var verticalLineX = currentEntry.value.position.x + currentEntry.value.size.width / 2

                    // Check for overlaps
                    val overlaps = completedBlueprintLines.filter { 
                        it.isVertical && it.start.x == verticalLineX &&
                        maxOf(it.start.y, it.end.y) > minOf(currentEntry.value.position.y + currentEntry.value.size.height, screenSize.height) &&
                        minOf(it.start.y, it.end.y) < maxOf(currentEntry.value.position.y + currentEntry.value.size.height, screenSize.height)
                    }
                    
                    if (overlaps.isNotEmpty()) {
                        verticalLineX += (overlaps.size * lineSpacingOffset)
                    }

                    val blueprintLine = BlueprintLine(
                        start = Offset(
                            x = verticalLineX,
                            y = currentEntry.value.position.y + currentEntry.value.size.height,
                        ),
                        end = Offset(
                            x = verticalLineX,
                            y = screenSize.height,
                        )
                    )

                    val intersectsAnyBlueprintItem = validBlueprintItems
                        .filter { it.value != currentEntry.value }
                        .any { blueprintLine.intersects(it.value) }

                    if (!intersectsAnyBlueprintItem) {

                        val width = min(measuredLineWidth, blueprintLine.length * minimumScale)

                        drawLine(
                            start = blueprintLine.start,
                            end = blueprintLine.end,
                            color = Color.White,
                            strokeWidth = width,
                        )

                        val upArrowPath = createArrowPath(
                            direction = Direction.Top,
                            tip = blueprintLine.start,
                            blueprintLine.length,
                        )

                        drawOutline(
                            outline = Outline.Generic(upArrowPath),
                            color = Color.White,
                        )

                        val downArrowPath = createArrowPath(
                            direction = Direction.Bottom,
                            tip = blueprintLine.end,
                            blueprintLine.length,
                        )

                        drawOutline(
                            outline = Outline.Generic(downArrowPath),
                            color = Color.White,
                        )

                        drawBlueprintLineLabel(textMeasurer, blueprintLine, backgroundColor)

                        completedBlueprintLines += blueprintLine
                    }
                }
            }
        }
    }
}
