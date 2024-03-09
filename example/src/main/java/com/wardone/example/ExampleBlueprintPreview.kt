package com.wardone.example

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
import com.wardone.bluprint.items.BlueprintItem
import com.wardone.bluprint.items.None
import com.wardone.bluprint.items.Specific
import com.wardone.bluprint.preview.BlueprintPreview

@Composable
fun ExampleLayout(
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

@Preview
@Composable
fun ExampleBlueprintPreview() {
    BlueprintPreview { geometryUpdated ->
        ExampleLayout(
            hero = {
                BlueprintItem(
                    modifier = Modifier
                        .width(80.dp)
                        .height(80.dp),
                    label = "Hero",
                    itemUpdated = geometryUpdated,
                )
            },
            header = {
                BlueprintItem(
                    modifier = Modifier
                        .width(144.dp)
                        .height(48.dp),
                    label = "Header",
                    itemUpdated = geometryUpdated,
                )
            },
            body = {
                BlueprintItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(288.dp),
                    label = "Body",
                    parentConnectionConfig = Specific(
                        top = None,
                    ),
                    itemUpdated = geometryUpdated,
                )
            },
            footer = {
                BlueprintItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    label = "Footer",
                    itemUpdated = geometryUpdated,
                )
            }
        )
    }
}
