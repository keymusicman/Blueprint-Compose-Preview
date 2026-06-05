package com.wardone.bluprint.items

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wardone.bluprint.constants.SemanticColors
import java.text.DecimalFormat
import java.util.UUID

@Composable
fun BlueprintItem(
    modifier: Modifier = Modifier,
    id: String = remember {
        UUID.randomUUID().toString()
    },
    label: String,
    parentConnectionConfig: ParentConnectionConfig = WherePossible,
    itemUpdated: (BlueprintItemData) -> Unit,
) {

    val decimalFormat = remember {
        DecimalFormat("0")
    }

    var itemSize by remember {
        mutableStateOf(
            IntSize(0, 0)
        )
    }

    Box(
        modifier = modifier
            .border(
                width = 2.dp,
                color = Color.White,
            )
            .background(
                color = SemanticColors.BlueprintBackground,
            )
            .onGloballyPositioned { layoutCoordinates ->
                
                // Android Studio Preview often reports 0x0 during zoom/tab transitions.
                // Ignore these spurious updates to retain the last known good layout size.
                if (layoutCoordinates.size.width > 0 && layoutCoordinates.size.height > 0) {
                    itemSize = layoutCoordinates.size

                    /* tell the parent about our latest position and size */
                    itemUpdated(
                        BlueprintItemData(
                            id = id,
                            label = label,
                            position = Offset(
                                x = layoutCoordinates.boundsInRoot().left,
                                y = layoutCoordinates.boundsInRoot().top,
                            ),
                            size = Size(
                                width = layoutCoordinates.size.width.toFloat(),
                                height = layoutCoordinates.size.height.toFloat(),
                            ),
                            parentConnectionConfig = parentConnectionConfig,
                        )
                    )
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        /* draw repeated 45 degree lines */
        Canvas(
            modifier = Modifier.fillMaxSize(),
        ) {

            val path = Path()

            path.moveTo(size.width, 0f)
            path.lineTo(size.width, size.height)
            path.lineTo(0f, size.height)
            path.lineTo(0f, 0f)

            clipPath(
                path = path,
                clipOp = ClipOp.Intersect
            ) {

                val spacing = size.width / 10
                var start = spacing

                do {
                    drawLine(
                        start = Offset(x = 0f, y = start),
                        end = Offset(x = start, y = 0f),
                        color = Color.White,
                        strokeWidth = 2f,
                    )
                    start += spacing
                } while (start < size.width + size.height)
            }
        }
        
        // Tiered scaling: reduce vertical padding first, then scale font
        val density = LocalDensity.current
        val itemDpHeight = density.run { itemSize.height.toDp() }
        val itemDpWidth = density.run { itemSize.width.toDp() }

        var fontSize = 12.sp
        var vPadding = 2.dp
        var hPadding = 2.dp
        
        // Thresholds are higher for active items because they have 2 lines of text (label + dimensions)
        val fullHeightNeeded = 48.dp
        val textOnlyHeight = 36.dp
        val fullWidthNeeded = 80.dp

        // 1. Height-based scaling (Linear Progress)
        if (itemDpHeight < fullHeightNeeded) {
            if (itemDpHeight >= textOnlyHeight) {
                // Stage 1: Linearly reduce padding from 2dp to 0dp
                val progress = (itemDpHeight - textOnlyHeight) / (fullHeightNeeded - textOnlyHeight)
                vPadding = (2 * progress).dp
            } else {
                // Stage 2: Vertical padding is gone, scale font
                vPadding = 0.dp
                val heightScale = (itemDpHeight / textOnlyHeight).coerceAtLeast(0.4f)
                fontSize = (12 * heightScale).sp
                hPadding = (2 * heightScale).dp
            }
        }

        // 2. Width-based scaling (Simple)
        if (itemDpWidth < fullWidthNeeded) {
            val widthScale = (itemDpWidth / fullWidthNeeded).coerceAtLeast(0.4f)
            val currentFontValue = fontSize.value
            val widthScaledFontValue = 12 * widthScale
            if (widthScaledFontValue < currentFontValue) {
                fontSize = widthScaledFontValue.sp
                hPadding = (2 * (widthScaledFontValue / 12f)).dp
            }
        }

        if (itemSize.width > (itemSize.height * 2)) {
            Row(
                modifier = Modifier
                    .background(SemanticColors.BlueprintBackground)
                    .border(1.dp, Color.White.copy(alpha = 0.7f))
                    .padding(horizontal = hPadding, vertical = vPadding),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    fontWeight = FontWeight.Medium,
                    fontSize = fontSize,
                    text = label,
                )
                Text(
                    fontSize = fontSize,
                    fontWeight = FontWeight.Medium,
                    text = density.run {
                        val width = decimalFormat.format(itemSize.width.toDp().value)
                        val height = decimalFormat.format(itemSize.height.toDp().value)
                        "${width}x${height}"
                    },
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .border(1.dp, Color.White.copy(alpha = 0.7f))
                    .padding(horizontal = hPadding, vertical = vPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    fontWeight = FontWeight.Medium,
                    fontSize = fontSize,

                    text = label,
                )
                Text(
                    fontSize = fontSize,
                    fontWeight = FontWeight.Medium,
                    text = density.run {
                        val width = decimalFormat.format(itemSize.width.toDp().value)
                        val height = decimalFormat.format(itemSize.height.toDp().value)
                        "${width}x${height}"
                    },
                )
            }
        }
    }
}
