package com.wardone.example

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wardone.bluprint.items.BlueprintItem
import com.wardone.bluprint.preview.BlueprintPreview

@Composable
fun ExampleSimpleRow(
    items: List<@Composable () -> Unit>
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp),
        horizontalArrangement = Arrangement.spacedBy(40.dp)
    ) {
        items.forEach {
            it()
        }
    }
}

@Preview
@Composable
fun ExampleSimpleRowBlueprintPreview() {
    BlueprintPreview { itemUpdated ->
        ExampleSimpleRow(
            listOf(
                {
                    BlueprintItem(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(100.dp),
                        label = "Item 1",
                        itemUpdated = itemUpdated,
                    )
                },
                {
                    BlueprintItem(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(100.dp),
                        label = "Item 2",
                        itemUpdated = itemUpdated,
                    )
                },
            )
        )
    }
}