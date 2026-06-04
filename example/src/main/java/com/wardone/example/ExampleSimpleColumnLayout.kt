package com.wardone.example

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wardone.bluprint.items.BlueprintItem
import com.wardone.bluprint.preview.BlueprintPreview
import com.wardone.bluprint.preview.PassiveBlueprintPreview

@Composable
fun ExampleSimpleColumn(
    items: List<@Composable () -> Unit>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp),
        verticalArrangement = Arrangement.spacedBy(40.dp)
    ) {
        items.forEach {
            it()
        }
    }
}

@Preview
@Composable
fun ExampleSimpleColumnBlueprintPreview() {
    BlueprintPreview { itemUpdated ->
        ExampleSimpleColumn(
            listOf(
                {
                    BlueprintItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        label = "Item 1",
                        itemUpdated = itemUpdated,
                    )
                },
                {
                    BlueprintItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        label = "Item 2",
                        itemUpdated = itemUpdated,
                    )
                },
                {
                    BlueprintItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        label = "Item 3",
                        itemUpdated = itemUpdated,
                    )
                },
                {
                    BlueprintItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        label = "Item 3",
                        itemUpdated = itemUpdated,
                    )
                },
                {
                    BlueprintItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        label = "Item 3",
                        itemUpdated = itemUpdated,
                    )
                },
                {
                    BlueprintItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        label = "Item 3",
                        itemUpdated = itemUpdated,
                    )
                }
            )
        )
    }
}


@Preview
@Composable
fun PassiveExampleSimpleColumnBlueprintPreview() {
    PassiveBlueprintPreview {
        ExampleSimpleColumn(
            listOf(
                { Box(modifier = Modifier.height(100.dp)) {} },
                { androidx.compose.material3.Text("Passive Item 2", modifier = Modifier.fillMaxWidth().height(100.dp)) }
            )
        )
    }
}
