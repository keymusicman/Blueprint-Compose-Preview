package uk.co.gusward.example.custom

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import uk.co.gusward.bluprint.preview.BlueprintPreview
import uk.co.gusward.bluprint.preview.blueprintId

@Composable
fun ExampleFeatureLayout(
    hero: @Composable () -> Unit,
    header: @Composable () -> Unit,
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
                end.linkTo(parent.end)
            }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {

                hero()
                header()
            }
        }
        Box(
            modifier = Modifier.constrainAs(bodyRef) {
                top.linkTo(headerRef.bottom, 20.dp)
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

@Preview
@Composable
fun ExampleFeatureLayoutPreview() {
    BlueprintPreview {
        ExampleFeatureLayout(
            hero = {
                Box(
                    modifier = Modifier
                        .blueprintId("Hero")
                        .width(80.dp)
                        .height(80.dp),
                )
            },
            header = {
                Box(
                    modifier = Modifier
                        .blueprintId("Header")
                        .width(144.dp)
                        .height(48.dp),
                )
            },
            body = {
                Box(
                    modifier = Modifier
                        .blueprintId("Body")
                        .fillMaxWidth()
                        .height(288.dp),
                )
            },
            footer = {
                Box(
                    modifier = Modifier
                        .blueprintId("Footer")
                        .fillMaxWidth()
                        .height(50.dp),
                )
            }
        )
    }
}
