package uk.co.gusward.example.custom

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import uk.co.gusward.bluprint.preview.BlueprintPreview

@Composable
fun ExampleFeatureLayout(
    header: @Composable () -> Unit,
    body: @Composable () -> Unit,
    footer: @Composable () -> Unit,
) {
    ConstraintLayout(
        modifier = Modifier
            .background(
                color = Color.Gray
            )
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
    MaterialTheme {
        ExampleFeatureLayout(
            header = {
                /**
                 * re-use the component preview to test full feature scenario
                 */
                ExampleComponentStandardPreview()
            },
            body = {
                Box(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.background,
                            shape = RoundedCornerShape(10.dp),
                        ).padding(12.dp)
                ) {
                    Text(
                        "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since 1966, when designers at Letraset and James Mosley, the librarian at St Bride Printing Library, took a 1914 Cicero translation and scrambled it to make dummy text for Letraset's Body Type sheets. It has survived not only many decades, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised thanks to these sheets and more recently with desktop publishing software including versions of Lorem Ipsum.",
                        maxLines = 5,)
                }
            },
            footer = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("Test label")
                            Switch(
                                checked = false,
                                onCheckedChange = {}
                            )
                        }
                        Button(
                            onClick = {}
                        ) {
                            Text("Test button")
                        }
                    }

                }
            }
        )
    }
}

@Preview
@Composable
fun ExampleFeatureLayoutBlueprintPreview() {
    BlueprintPreview {
        ExampleFeatureLayoutPreview()
    }
}
