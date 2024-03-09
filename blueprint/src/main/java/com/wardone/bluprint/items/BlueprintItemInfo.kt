package com.wardone.bluprint.items

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size

data class BlueprintItemInfo(
    val label: String,
    val position: Offset,
    val size: Size,
    val parentConnectionConfig: ParentConnectionConfig,
) {

    infix fun canSeeBelow(other: BlueprintItemInfo): Boolean {

        val bottom = position.y + size.height
        val centerBottom = position.x + size.width / 2
        val otherRight = other.position.x + other.size.width

        val otherIsBelow = other.position.y > bottom
        val otherInLineOfSight = centerBottom >= other.position.x && centerBottom <= otherRight

        return otherIsBelow && otherInLineOfSight
    }

    infix fun canSeeRight(other: BlueprintItemInfo): Boolean {

        val right = position.x + size.width
        val centerVertical = position.y + size.height / 2
        val otherBottom = other.position.x + other.size.width

        val otherIsToTheRight = other.position.x > right
        val otherInLineOfSight = centerVertical >= other.position.y && centerVertical <= otherBottom

        return otherIsToTheRight && otherInLineOfSight
    }

    infix fun canSeeLeft(other: BlueprintItemInfo): Boolean {

        val otherRight = other.position.x + other.size.width
        val centerVertical = position.y + (size.height / 2)
        val otherBottom = other.position.x + other.size.width

        val otherIsToTheLeft = otherRight < position.x
        val otherInLineOfSight = centerVertical >= other.position.y && centerVertical <= otherBottom

        return otherIsToTheLeft && otherInLineOfSight
    }

    infix fun canSeeAbove(other: BlueprintItemInfo): Boolean {

        val otherBottom = other.position.y + other.size.height
        val centerBottom = position.x + (size.width / 2)
        val otherRight = other.position.x + other.size.width

        val otherIsAbove = position.y > otherBottom
        val otherInLineOfSight = centerBottom >= other.position.x && centerBottom <= otherRight

        return otherIsAbove && otherInLineOfSight
    }
}
