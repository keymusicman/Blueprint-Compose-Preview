package uk.co.gusward.example

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import uk.co.gusward.bluprint.preview.BlueprintPreview
import uk.co.gusward.bluprint.preview.blueprintId

@Composable
fun Block(w: Int, h: Int, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .width(w.dp)
            .height(h.dp)
            .background(Color.White.copy(alpha = 0.8f))
    )
}

@Composable
fun BlueprintLogo() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF003153)) // Blueprint blue background
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        ConstraintLayout {
            val (blueprint, dot, compose, preview) = createRefs()

            val logoColor = Color.White.copy(alpha = 0.8f)
            val mainFontSize = 80.sp
            val subFontSize = 40.sp

            val tightTextStyle = TextStyle(
                platformStyle = PlatformTextStyle(includeFontPadding = false),
                lineHeightStyle = LineHeightStyle(
                    alignment = LineHeightStyle.Alignment.Center,
                    trim = LineHeightStyle.Trim.Both
                )
            )

            Text(
                text = "blueprint",
                color = logoColor,
                fontSize = mainFontSize,
                fontWeight = FontWeight.Black,
                style = tightTextStyle.copy(lineHeight = mainFontSize),
                modifier = Modifier
                    .constrainAs(blueprint) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                    }
                    .blueprintId("blueprint")
            )

            // Full stop block
            Block(
                w = 11,
                h = 11,
                modifier = Modifier
                    .constrainAs(dot) {
                        bottom.linkTo(blueprint.bottom, margin = 18.dp) // Offset slightly lower than before for perfect alignment
                        start.linkTo(blueprint.end, margin = 8.dp)
                    }
                    .blueprintId(".")
            )

            Text(
                text = "compose",
                color = logoColor,
                fontSize = subFontSize,
                fontWeight = FontWeight.Normal, // Differentiate slightly from preview
                style = tightTextStyle.copy(lineHeight = subFontSize),
                modifier = Modifier
                    .constrainAs(compose) {
                        top.linkTo(blueprint.bottom, margin = 16.dp)
                        start.linkTo(blueprint.start) // Align to start of blueprint
                    }
                    .blueprintId("compose")
            )

            Text(
                text = "preview",
                color = logoColor,
                fontSize = subFontSize,
                fontWeight = FontWeight.Bold,
                style = tightTextStyle.copy(lineHeight = subFontSize),
                modifier = Modifier
                    .constrainAs(preview) {
                        top.linkTo(compose.top)
                        end.linkTo(blueprint.end) // Align to end of blueprint
                    }
                    .blueprintId("preview")
            )
        }
    }
}

@Preview(widthDp = 500,  heightDp = 220)
@Composable
fun BlueprintLogoStandardPreview() {
    BlueprintLogo()
}

@Preview(widthDp = 500,  heightDp = 220)
@Composable
fun BlueprintLogoPreview() {
    BlueprintPreview(
        contentAlpha = 1f,
        backgroundAlpha = 0.5f,
    ) {
        BlueprintLogoStandardPreview()
    }
}
