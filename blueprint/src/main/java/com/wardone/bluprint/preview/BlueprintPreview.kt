package com.wardone.bluprint.preview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import com.wardone.bluprint.grid.BlueprintGrid
import com.wardone.bluprint.items.BlueprintItemData

import android.view.ViewTreeObserver
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.platform.LocalView

@Composable
fun BlueprintPreview(
    content: @Composable (blueprintItemUpdated: (BlueprintItemData) -> Unit) -> Unit
) {
    BlueprintTheme {

        var blueprintItemDataState by remember {
            mutableStateOf<Map<String, BlueprintItemData>>(mutableMapOf())
        }
        var refreshKey by remember { mutableIntStateOf(0) }
        val view = LocalView.current

        // Hook directly into the Android View lifecycle instead of using coroutines
        DisposableEffect(view) {
            val listener = ViewTreeObserver.OnGlobalLayoutListener {
                refreshKey++
            }
            view.viewTreeObserver.addOnGlobalLayoutListener(listener)
            onDispose {
                view.viewTreeObserver.removeOnGlobalLayoutListener(listener)
            }
        }

        BlueprintGrid(
            gridSize = 24.dp,
            blueprintItems = blueprintItemDataState,
            refreshKey = refreshKey
        ) {
            content { blueprintItemData ->

                /**
                 * as blueprint items get laid out in the preview, they will keep us updated
                 * here with their most recent position, size etc. then we just update our
                 * state to refresh the blueprint grid, who will use the data to draw measured
                 * lines, labels etc.
                 */
                blueprintItemDataState = blueprintItemDataState
                    .toMutableMap()
                    .apply {
                        put(blueprintItemData.id, blueprintItemData)
                    }
            }
        }
    }
}
