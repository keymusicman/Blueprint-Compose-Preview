package com.wardone.bluprint.preview.utils

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import com.wardone.bluprint.constants.Direction

fun createArrowPath(
    direction: Direction,
    tip: Offset,
) : Path = Path().apply {

    val hypotenuseLength = 20f
    val baseLength = 15f

    moveTo(
        tip.x,
        tip.y,
    )
    when (direction) {
        Direction.Left -> {
            lineTo(
                tip.x + hypotenuseLength,
                tip.y - baseLength,
            )
            lineTo(
                tip.x + hypotenuseLength,
                tip.y + baseLength,
            )
        }

        Direction.Top -> {
            lineTo(
                tip.x + baseLength,
                tip.y + hypotenuseLength,
            )
            lineTo(
                tip.x - baseLength,
                tip.y + hypotenuseLength,
            )
        }

        Direction.Right -> {
            lineTo(
                tip.x - hypotenuseLength,
                tip.y - baseLength,
            )
            lineTo(
                tip.x - hypotenuseLength,
                tip.y + baseLength,
            )
        }

        Direction.Bottom -> {
            lineTo(
                tip.x + baseLength,
                tip.y - hypotenuseLength,
            )
            lineTo(
                tip.x - baseLength,
                tip.y - hypotenuseLength,
            )
        }
    }
    lineTo(
        tip.x,
        tip.y,
    )
    close()
}
