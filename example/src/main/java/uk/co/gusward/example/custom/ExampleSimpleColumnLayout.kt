package uk.co.gusward.example.custom

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uk.co.gusward.bluprint.preview.BlueprintPreview

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
    BlueprintPreview {
        ExampleSimpleColumn(
            listOf(
                { Box(modifier = Modifier.height(100.dp)) {} },
                { Text("Passive Item 2", modifier = Modifier.fillMaxWidth().height(100.dp)) }
            )
        )
    }
}

