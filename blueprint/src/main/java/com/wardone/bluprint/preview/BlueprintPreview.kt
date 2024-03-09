package com.wardone.bluprint.preview

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.wardone.bluprint.items.BlueprintItemData

@Composable
fun BlueprintPreview(
    content: @Composable (geometryUpdated: (BlueprintItemData) -> Unit) -> Unit
) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            background = Color(0xFF003153),
            onBackground = Color.White,
        ),
        content = {

            var blueprintItemGeometries by remember {
                mutableStateOf<Map<String, BlueprintItemData>>(mutableMapOf())
            }

            BlueprintGrid(
                gridSize = 24.dp,
                blueprintItemGeometries
            ) {
                ProvideTextStyle(
                    value = TextStyle(Color.White)
                ) {
                    content { geometry ->
                        println("geometry updated")
                        blueprintItemGeometries = blueprintItemGeometries
                            .toMutableMap()
                            .apply {
                                put(geometry.label, geometry)
                            }
                    }
                }
            }
        }
    )
}
