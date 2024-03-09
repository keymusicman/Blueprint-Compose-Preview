package com.wardone.bluprint.preview

import androidx.compose.ui.geometry.Offset
import kotlin.math.pow
import kotlin.math.sqrt

data class BlueprintLine(
    val start: Offset,
    val end: Offset,
) {

    override fun equals(other: Any?): Boolean {

        val otherLine = other as? BlueprintLine ?: return false

        val offsetsMatchExactly = start == otherLine.start && end == otherLine.end
        val offsetsMatchReversed = start == otherLine.end && end == otherLine.start

        return offsetsMatchExactly || offsetsMatchReversed
    }

    fun clashes(otherLine: BlueprintLine): Boolean = if (isVertical && otherLine.isVertical) {
        start.x == otherLine.start.x && otherLine.start.y >= start.y && otherLine.start.y < end.y
    } else if (!isVertical && !otherLine.isVertical) {
        start.y == otherLine.start.y && otherLine.start.x >= start.x && otherLine.end.x < end.x
    } else {
        false
    }

    /**
     * √((x2 – x1)² + (y2 – y1)²)
     */
    fun length() = sqrt(
        x = (end.x - start.x).pow(n = 2)
                + (end.y - start.y).pow(n = 2))

    override fun hashCode(): Int {
        var result = start.hashCode()
        result = 31 * result + end.hashCode()
        return result
    }

    val isVertical: Boolean = start.y != end.y

    val midPoint: Offset = Offset(
        x = (start.x + end.x) / 2,
        y = (start.y + end.y) / 2
    )
}
