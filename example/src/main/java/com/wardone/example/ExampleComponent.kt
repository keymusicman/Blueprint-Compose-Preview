package com.wardone.example

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wardone.bluprint.items.BlueprintItem
import com.wardone.bluprint.items.None
import com.wardone.bluprint.items.Specific
import com.wardone.bluprint.preview.BlueprintPreview

@Composable
fun ExampleComponent(
    image: @Composable () -> Unit,
    title: @Composable () -> Unit,
    body: @Composable () -> Unit,
    action: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier
            .size(300.dp)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(30.dp),
        ) {
            image()
            title()
        }
        body()
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            action()
        }
    }
}

@Preview(device = "spec:width=300dp,height=300dp,dpi=440")
@Composable
fun ExampleComponentBlueprintPreview() {
    BlueprintPreview { itemUpdated ->
        ExampleComponent(
            image = {
                BlueprintItem(
                    modifier = Modifier
                        .size(100.dp),
                    label = "Image",
                    itemUpdated = itemUpdated,
                )
            },
            title = {
                BlueprintItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(30.dp),
                    label = "Title",
                    itemUpdated = itemUpdated,
                )
            },
            body = {
                BlueprintItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    label = "Body",
                    itemUpdated = itemUpdated,
                    parentConnectionConfig = Specific(
                        top = None,
                    )
                )
            },
            action = {
                BlueprintItem(
                    modifier = Modifier
                        .size(50.dp),
                    label = "Action",
                    itemUpdated = itemUpdated,
                )
            }
        )
    }
}