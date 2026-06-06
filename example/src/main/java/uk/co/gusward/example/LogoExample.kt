package uk.co.gusward.example

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uk.co.gusward.bluprint.preview.PassiveBlueprintPreview
import uk.co.gusward.bluprint.preview.blueprintId
import androidx.constraintlayout.compose.ConstraintLayout

import androidx.compose.material3.Text
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.PlatformTextStyle

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
            .fillMaxWidth()
            .background(Color(0xFF003153)) // Blueprint blue background
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        ConstraintLayout {
            val (blueprint, dot, compose, preview) = createRefs()

            val logoColor = Color.White.copy(alpha = 0.8f)
            val mainFontSize = 80.sp
            val subFontSize = 40.sp

            Text(
                text = "blueprint",
                color = logoColor,
                fontSize = mainFontSize,
                fontWeight = FontWeight.Black,
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
                style = TextStyle(
                    lineHeight = 32.sp,
                    platformStyle = PlatformTextStyle(includeFontPadding = false)
                ),
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
                style = TextStyle(
                    lineHeight = 32.sp,
                    platformStyle = PlatformTextStyle(includeFontPadding = false)
                ),
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

@Preview(widthDp = 500, heightDp = 220)
@Composable
fun BlueprintLogoStandardPreview() {
    BlueprintLogo()
}

@Preview(widthDp = 500, heightDp = 220)
@Composable
fun BlueprintLogoPassivePreview() {
    PassiveBlueprintPreview(
        contentAlpha = 1f,
        backgroundAlpha = 0.5f,
    ) {
        BlueprintLogo()
    }
}
