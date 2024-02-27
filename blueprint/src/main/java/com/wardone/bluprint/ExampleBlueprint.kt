package com.wardone.bluprint

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.constraintlayout.compose.ConstraintLayout
import java.text.DecimalFormat
import kotlin.math.pow
import kotlin.math.sqrt

@Composable
fun ExampleBlueprint(
    header: @Composable () -> Unit,
    otherHeader: @Composable () -> Unit,
    body: @Composable () -> Unit,
    footer: @Composable () -> Unit,
) {
    ConstraintLayout(
        modifier = Modifier
            .padding(48.dp)
            .fillMaxSize()
    ) {

        val (headerRef, bodyRef, footerRef) = createRefs()

        Box(
            modifier = Modifier.constrainAs(headerRef) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
            }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {

                otherHeader()
                header()
            }
        }
        Box(
            modifier = Modifier.constrainAs(bodyRef) {
                top.linkTo(headerRef.bottom, 20.dp)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        ) {
            body()
        }
        Box(
            modifier = Modifier.constrainAs(footerRef) {
                top.linkTo(bodyRef.bottom, 20.dp)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        ) {
            footer()
        }
    }
}

data class BlueprintItemGeometry(
    val label: String,
    val position: Offset,
    val size: Size,
) {

    infix fun canSeeBelow(other: BlueprintItemGeometry): Boolean {

        val bottom = position.y + size.height
        val centerBottom = position.x + size.width / 2
        val otherRight = other.position.x + other.size.width

        val otherIsBelow = other.position.y > bottom
        val otherInLineOfSight = centerBottom >= other.position.x && centerBottom <= otherRight

        return otherIsBelow && otherInLineOfSight
    }

    infix fun canSeeRight(other: BlueprintItemGeometry): Boolean {

        val right = position.x + size.width
        val centerVertical = position.y + size.height / 2
        val otherBottom = other.position.x + other.size.width

        val otherIsToTheRight = other.position.x > right
        val otherInLineOfSight = centerVertical >= other.position.y && centerVertical <= otherBottom

        return otherIsToTheRight && otherInLineOfSight
    }

    infix fun canSeeLeft(other: BlueprintItemGeometry): Boolean {

        val otherRight = other.position.x + other.size.width
        val centerVertical = position.y + (size.height / 2)
        val otherBottom = other.position.x + other.size.width

        val otherIsToTheLeft = otherRight < position.x
        val otherInLineOfSight = centerVertical >= other.position.y && centerVertical <= otherBottom

        return otherIsToTheLeft && otherInLineOfSight
    }

    infix fun canSeeAbove(other: BlueprintItemGeometry): Boolean {

        val otherBottom = other.position.y + other.size.height
        val centerBottom = position.x + (size.width / 2)
        val otherRight = other.position.x + other.size.width

        val otherIsAbove = position.y > otherBottom
        val otherInLineOfSight = centerBottom >= other.position.x && centerBottom <= otherRight

        return otherIsAbove && otherInLineOfSight
    }
}

@Composable
fun BlueprintItem(
    modifier: Modifier,
    label: String,
    geometryUpdated: (BlueprintItemGeometry) -> Unit,
) {

    var itemSize by remember {
        mutableStateOf(
            IntSize(0, 0)
        )
    }

    Box(
        modifier = modifier
            .border(
                width = 2.dp,
                color = Color.White,
            )
            .background(
                color = MaterialTheme.colorScheme.background.copy(
                    alpha = 0.7f
                ),
            )
            .onGloballyPositioned { layoutCoordinates ->
                itemSize = layoutCoordinates.size
                geometryUpdated(
                    BlueprintItemGeometry(
                        label = label,
                        position = Offset(
                            x = layoutCoordinates.boundsInRoot().left,
                            y = layoutCoordinates.boundsInRoot().top,
                        ),
                        size = Size(
                            width = layoutCoordinates.size.width.toFloat(),
                            height = layoutCoordinates.size.height.toFloat(),
                        )
                    )
                )
            },
        contentAlignment = Alignment.Center,
    ) {
        /* draw repeated 45 degree lines */
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

                val spacing = size.width / 10
                var start = spacing

                do {
                    drawLine(
                        start = Offset(x = 0f, y = start),
                        end = Offset(x = start, y = 0f),
                        color = Color.White,
                        strokeWidth = 2f,
                    )
                    start += spacing
                } while (start < size.width + size.height)
            }
        }
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .border(1.dp, Color.White.copy(alpha = 0.7f)),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                modifier = Modifier
                    .padding(
                        start = 6.dp,
                        top = 6.dp,
                        end = 6.dp,
                        bottom = 1.dp
                    ),
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,

                text = label,
            )
            Text(
                modifier = Modifier
                    .padding(
                        start = 6.dp,
                        top = 1.dp,
                        end = 6.dp,
                        bottom = 6.dp
                    ),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                text = "${itemSize.width}x${itemSize.height}",
            )
        }
    }
}

data class BlueprintLine(
    val start: Offset,
    val end: Offset,
) {

    override fun equals(other: Any?): Boolean {

        val otherLine = other as? BlueprintLine ?: return false

        val offsetsMatchExactly = start == otherLine.start && end == otherLine.end
        val offsetsMatchReversed = start == otherLine.end && end == otherLine.start

        return offsetsMatchExactly || offsetsMatchReversed
    }

    fun clashes(otherLine: BlueprintLine): Boolean = if (isVertical && otherLine.isVertical) {
        start.x == otherLine.start.x && otherLine.start.y >= start.y && otherLine.start.y < end.y
    } else if (!isVertical && !otherLine.isVertical) {
        start.y == otherLine.start.y && otherLine.start.x >= start.x && otherLine.end.x < end.x
    } else {
        false
    }

    /**
     * √((x2 – x1)² + (y2 – y1)²)
     */
    fun length() = sqrt(
        x = (end.x - start.x).pow(n = 2)
                + (end.y - start.y).pow(n = 2))

    override fun hashCode(): Int {
        var result = start.hashCode()
        result = 31 * result + end.hashCode()
        return result
    }

    val isVertical: Boolean = start.y != end.y

    val midPoint: Offset = Offset(
        x = (start.x + end.x) / 2,
        y = (start.y + end.y) / 2
    )
}

enum class ArrowDirection {
    Left,
    Up,
    Right,
    Down,
}

fun createArrowPath(
    direction: ArrowDirection,
    tip: Offset,
) : Path = Path().apply {

    val hypotenuseLength = 20f
    val baseLength = 15f

    moveTo(
        tip.x,
        tip.y,
    )
    when (direction) {
        ArrowDirection.Left -> {
            lineTo(
                tip.x + hypotenuseLength,
                tip.y - baseLength,
            )
            lineTo(
                tip.x + hypotenuseLength,
                tip.y + baseLength,
            )
        }
        ArrowDirection.Up -> {
            lineTo(
                tip.x + baseLength,
                tip.y + hypotenuseLength,
            )
            lineTo(
                tip.x - baseLength,
                tip.y + hypotenuseLength,
            )
        }
        ArrowDirection.Right -> {
            lineTo(
                tip.x - hypotenuseLength,
                tip.y - baseLength,
            )
            lineTo(
                tip.x - hypotenuseLength,
                tip.y + baseLength,
            )
        }
        ArrowDirection.Down -> {
            lineTo(
                tip.x + baseLength,
                tip.y - hypotenuseLength,
            )
            lineTo(
                tip.x - baseLength,
                tip.y - hypotenuseLength,
            )
        }
    }
    lineTo(
        tip.x,
        tip.y,
    )
    close()
}

@Composable
fun BlueprintGrid(
    gridSize: Dp,
    itemGeometries: Map<String, BlueprintItemGeometry>,
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

            itemGeometries.forEach { currentEntry ->

                val completedBlueprintLines = mutableListOf<BlueprintLine>()

                /* draw VERTICAL connecting lines where there is direct line of sight */
                itemGeometries.values.filter { other ->
                    currentEntry.value != other && currentEntry.value canSeeBelow other
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
                        direction = ArrowDirection.Up,
                        tip = blueprintLine.start
                    )

                    drawOutline(
                        outline = Outline.Generic(upArrowPath),
                        color = Color.White,
                    )

                    val downArrowPath = createArrowPath(
                        direction = ArrowDirection.Down,
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
                itemGeometries.values.filter { other ->
                    currentEntry.value != other && currentEntry.value canSeeRight other
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
                        direction = ArrowDirection.Left,
                        tip = blueprintLine.start
                    )

                    drawOutline(
                        outline = Outline.Generic(leftArrowPath),
                        color = Color.White,
                    )

                    val rightArrowPath = createArrowPath(
                        direction = ArrowDirection.Right,
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

                val parentDirectLeft = itemGeometries.values.none { other ->
                    currentEntry.value canSeeLeft other
                }

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
                        direction = ArrowDirection.Left,
                        tip = blueprintLine.end
                    )

                    drawOutline(
                        outline = Outline.Generic(leftArrowPath),
                        color = Color.White,
                    )

                    val rightArrowPath = createArrowPath(
                        direction = ArrowDirection.Right,
                        tip = blueprintLine.start
                    )

                    drawOutline(
                        outline = Outline.Generic(rightArrowPath),
                        color = Color.White,
                    )

                    drawBlueprintLineLabel(textMeasurer, blueprintLine, backgroundColor)
                }

                val parentDirectTop = itemGeometries.values.none { other ->
                    currentEntry.value canSeeAbove other
                }

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
                        direction = ArrowDirection.Up,
                        tip = blueprintLine.end
                    )

                    drawOutline(
                        outline = Outline.Generic(upArrowPath),
                        color = Color.White,
                    )

                    val downArrowPath = createArrowPath(
                        direction = ArrowDirection.Down,
                        tip = blueprintLine.start
                    )

                    drawOutline(
                        outline = Outline.Generic(downArrowPath),
                        color = Color.White,
                    )

                    drawBlueprintLineLabel(textMeasurer, blueprintLine, backgroundColor)
                }

                val parentDirectRight = itemGeometries.values.none { other ->
                    currentEntry.value canSeeRight other
                }

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
                        direction = ArrowDirection.Left,
                        tip = blueprintLine.start
                    )

                    drawOutline(
                        outline = Outline.Generic(leftArrowPath),
                        color = Color.White,
                    )

                    val rightArrowPath = createArrowPath(
                        direction = ArrowDirection.Right,
                        tip = blueprintLine.end
                    )

                    drawOutline(
                        outline = Outline.Generic(rightArrowPath),
                        color = Color.White,
                    )

                    drawBlueprintLineLabel(textMeasurer, blueprintLine, backgroundColor)
                }

                val parentDirectBottom = itemGeometries.values.none { other ->
                    currentEntry.value canSeeBelow other
                }

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
                        direction = ArrowDirection.Up,
                        tip = blueprintLine.start
                    )

                    drawOutline(
                        outline = Outline.Generic(upArrowPath),
                        color = Color.White,
                    )

                    val downArrowPath = createArrowPath(
                        direction = ArrowDirection.Down,
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

private fun DrawScope.drawBlueprintLineLabel(
    textMeasurer: TextMeasurer,
    blueprintLine: BlueprintLine,
    backgroundColor: Color
) {

    val format = DecimalFormat("0.##")

    val measuredText = textMeasurer.measure(
        text = AnnotatedString(
            text = format.format(blueprintLine.length().toDp().value),
            spanStyle = SpanStyle(
                fontSize = 12.sp,

                )
        ),
        style = TextStyle(Color.White),
    )

    val textTopLeft = if (blueprintLine.isVertical) {
        blueprintLine.midPoint + Offset(
            x = 12f,
            y = -1f * (measuredText.size.height / 2f),
        )
    } else {
        blueprintLine.midPoint + Offset(
            x = -1f * (measuredText.size.width / 2f),
            y = 3f,
        )
    }

    drawRect(
        color = backgroundColor,
        topLeft = textTopLeft,
        size = measuredText.size.toSize(),
    )

    drawText(
        textLayoutResult = measuredText,
        topLeft = textTopLeft,
    )
}

@Composable
fun BlueprintPreview(
    content: @Composable (geometryUpdated: (BlueprintItemGeometry) -> Unit) -> Unit
) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            background = Color(0xFF003153),
            onBackground = Color.White,
        ),
        content = {

            var blueprintItemGeometries by remember {
                mutableStateOf<Map<String, BlueprintItemGeometry>>(mutableMapOf())
            }

            BlueprintGrid(
                gridSize = 24.dp,
                blueprintItemGeometries
            ) {
                ProvideTextStyle(
                    value = TextStyle(Color.White)
                ) {
                    content { geometry ->
                        println("geometry updated")
                        blueprintItemGeometries = blueprintItemGeometries
                            .toMutableMap()
                            .apply {
                                put(geometry.label, geometry)
                            }
                    }
                }
            }
        }
    )
}

@Preview
@Composable
fun ExampleBlueprintPreview() {
    BlueprintPreview { geometryUpdated ->
        ExampleBlueprint(
            header = {
                BlueprintItem(
                    modifier = Modifier
                        .width(144.dp)
                        .height(48.dp),
                    label = "Header",
                    geometryUpdated = geometryUpdated,
                )
            },
            otherHeader = {
                BlueprintItem(
                    modifier = Modifier
                        .width(80.dp)
                        .height(80.dp),
                    label = "Hero",
                    geometryUpdated = geometryUpdated,
                )
            },
            body = {
                BlueprintItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(288.dp),
                    label = "Body",
                    geometryUpdated = geometryUpdated,
                )
            },
            footer = {
                BlueprintItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    label = "Footer",
                    geometryUpdated = geometryUpdated,
                )
            }
        )
    }
}
