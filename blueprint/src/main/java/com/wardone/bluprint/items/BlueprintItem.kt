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
        if (itemSize.width > (itemSize.height * 2)) {
            Row(
                modifier = Modifier
                    .background(SemanticColors.BlueprintBackground)
                    .border(1.dp, Color.White.copy(alpha = 0.7f))
                    .padding(2.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    text = label,
                )
                Text(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    text = LocalDensity.current.run {
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
                    .padding(2.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,

                    text = label,
                )
                Text(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    text = LocalDensity.current.run {
                        val width = decimalFormat.format(itemSize.width.toDp().value)
                        val height = decimalFormat.format(itemSize.height.toDp().value)
                        "${width}x${height}"
                    },
                )
            }
        }
    }
}
