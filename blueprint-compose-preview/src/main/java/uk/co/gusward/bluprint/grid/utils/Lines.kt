package uk.co.gusward.bluprint.grid.utils

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import uk.co.gusward.bluprint.grid.BlueprintLine
import java.text.DecimalFormat
import kotlin.math.min

fun DrawScope.drawBlueprintLineLabel(
    textMeasurer: TextMeasurer,
    blueprintLine: BlueprintLine,
    backgroundColor: Color,
    allLines: List<BlueprintLine> = emptyList()
) {

    val format = DecimalFormat("0.##")

    val measuredText = textMeasurer.measure(
        text = AnnotatedString(
            text = format.format(blueprintLine.length.toDp().value),
            spanStyle = SpanStyle(
                fontSize = min((blueprintLine.length * 0.15f), 12f).sp
            )
        ),
        style = TextStyle(Color.White),
    )

    val baseTextTopLeft = if (blueprintLine.isVertical) {
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

    var textTopLeft = baseTextTopLeft
    val textSize = measuredText.size.toSize()

    val collision = allLines.any { 
        it != blueprintLine && it.intersects(
            Rect(
                textTopLeft, 
                Offset(textTopLeft.x + textSize.width, textTopLeft.y + textSize.height)
            )
        ) 
    }

    if (collision) {
        val step = 10f
        val maxSteps = 10
        // Try shifting up/down (for vertical) or left/right (for horizontal)
        for (i in 1..maxSteps) {
            val offsetValue = i * step
            val candidates = if (blueprintLine.isVertical) {
                listOf(Offset(0f, -offsetValue), Offset(0f, offsetValue))
            } else {
                listOf(Offset(-offsetValue, 0f), Offset(offsetValue, 0f))
            }

            val bestCandidate = candidates.firstOrNull { candidateOffset ->
                val candidateTopLeft = baseTextTopLeft + candidateOffset
                val candidateRect = Rect(
                    candidateTopLeft,
                    Offset(candidateTopLeft.x + textSize.width, candidateTopLeft.y + textSize.height)
                )
                !allLines.any { it != blueprintLine && it.intersects(candidateRect) }
            }

            if (bestCandidate != null) {
                textTopLeft = baseTextTopLeft + bestCandidate
                break
            }
        }
    }

    drawRect(
        color = backgroundColor.copy(alpha = 1f),
        topLeft = textTopLeft,
        size = textSize,
    )

    drawText(
        textLayoutResult = measuredText,
        topLeft = textTopLeft,
    )
}
