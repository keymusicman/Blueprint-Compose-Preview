package uk.co.gusward.example.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uk.co.gusward.bluprint.preview.BlueprintPreview

@Composable
fun ExampleFabComponent() {
    Box(
        modifier = Modifier.wrapContentSize()
            .padding(100.dp),
        contentAlignment = Alignment.Center,
    ) {
        FloatingActionButton(
            onClick = { /* do nothing */ },
        ) {
            Text("+")
        }
    }
}

@Preview(showBackground = true, name = "1. Normal Preview")
@Composable
fun ExampleFabComponentNormalPreview() {
    ExampleFabComponent()
}

@Preview(showBackground = true, name = "2. Blueprint Preview")
@Composable
fun ExampleFabComponentPreview() {
    BlueprintPreview {
        ExampleFabComponent()
    }
}
