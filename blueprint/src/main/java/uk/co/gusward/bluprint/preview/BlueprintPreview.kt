package uk.co.gusward.bluprint.preview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import uk.co.gusward.bluprint.grid.BlueprintGrid
import uk.co.gusward.bluprint.items.BlueprintItemData

@Composable
fun BlueprintPreview(
    content: @Composable (blueprintItemUpdated: (BlueprintItemData) -> Unit) -> Unit
) {
    BlueprintTheme {

        var blueprintItemDataState by remember {
            mutableStateOf<Map<String, BlueprintItemData>>(emptyMap())
        }

        BlueprintGrid(
            gridSize = 24.dp,
            blueprintItems = blueprintItemDataState
        ) {
            content { blueprintItemData ->
                // Ignore empty layout updates
                if (blueprintItemData.size.width > 0 && blueprintItemData.size.height > 0) {
                    blueprintItemDataState = blueprintItemDataState
                        .toMutableMap()
                        .apply {
                            put(blueprintItemData.id, blueprintItemData)
                        }
                }
            }
        }
    }
}
