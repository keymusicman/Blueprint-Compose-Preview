package uk.co.gusward.example.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uk.co.gusward.bluprint.preview.BlueprintPreview

@Composable
fun ExampleCheckboxComponent() {
    var checked by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.wrapContentSize()
            .padding(100.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Checkbox(
                checked = checked,
                onCheckedChange = { checked = it }
            )
            Text(
                text = "Accept terms"
            )
        }
    }
}

@Preview(showBackground = true, name = "1. Normal Preview")
@Composable
fun ExampleCheckboxComponentNormalPreview() {
    ExampleCheckboxComponent()
}

@Preview(showBackground = true, name = "2. Blueprint Preview")
@Composable
fun ExampleCheckboxComponentPreview() {
    BlueprintPreview {
        ExampleCheckboxComponent()
    }
}
