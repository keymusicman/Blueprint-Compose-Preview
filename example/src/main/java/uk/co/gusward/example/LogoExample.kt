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
            // Create refs for B.p.
            val (B, Dot1, P, Dot2) = createRefs()
            val space = 24.dp

            // b (90x135)
            ConstraintLayout(modifier = Modifier.constrainAs(B) { start.linkTo(parent.start) }.blueprintId("b")) {
                val (l, m, b, rb) = createRefs()
                Block(22, 135, Modifier.constrainAs(l) { start.linkTo(parent.start); top.linkTo(parent.top) })
                Block(46, 22, Modifier.constrainAs(m) { start.linkTo(l.end); top.linkTo(parent.top, 56.dp) })
                Block(46, 22, Modifier.constrainAs(b) { start.linkTo(l.end); top.linkTo(parent.top, 113.dp) })
                Block(22, 35, Modifier.constrainAs(rb) { start.linkTo(m.end); top.linkTo(m.bottom) })
            }

            // Dot 1 (22x22)
            ConstraintLayout(modifier = Modifier.constrainAs(Dot1) { 
                start.linkTo(B.end, space)
                bottom.linkTo(B.bottom)
            }.blueprintId(".")) {
                val (d) = createRefs()
                Block(22, 22, Modifier.constrainAs(d) { start.linkTo(parent.start); top.linkTo(parent.top) })
            }

            // P (lowercase 'p' drops below baseline, but for block consistency we'll make it uppercase P style 90x135)
            ConstraintLayout(modifier = Modifier.constrainAs(P) { start.linkTo(Dot1.end, space) }.blueprintId("p")) {
                val (l, t, m, r) = createRefs()
                Block(22, 135, Modifier.constrainAs(l) { start.linkTo(parent.start); top.linkTo(parent.top) })
                Block(46, 22, Modifier.constrainAs(t) { start.linkTo(l.end); top.linkTo(parent.top) })
                Block(46, 22, Modifier.constrainAs(m) { start.linkTo(l.end); top.linkTo(parent.top, 56.dp) })
                Block(22, 34, Modifier.constrainAs(r) { start.linkTo(t.end); top.linkTo(t.bottom) })
            }

            // Dot 2 (22x22)
            ConstraintLayout(modifier = Modifier.constrainAs(Dot2) { 
                start.linkTo(P.end, space)
                bottom.linkTo(P.bottom)
            }.blueprintId(".")) {
                val (d) = createRefs()
                Block(22, 22, Modifier.constrainAs(d) { start.linkTo(parent.start); top.linkTo(parent.top) })
            }
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
