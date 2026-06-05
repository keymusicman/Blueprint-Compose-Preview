package uk.co.gusward.bluprint.grid.utils

import androidx.compose.ui.geometry.Offset
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
    backgroundColor: Color
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
