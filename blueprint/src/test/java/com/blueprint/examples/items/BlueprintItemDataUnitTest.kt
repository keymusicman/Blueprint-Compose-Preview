package com.blueprint.examples.items

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import com.wardone.bluprint.items.BlueprintItemData
import com.wardone.bluprint.items.WherePossible
import org.junit.Test

class BlueprintItemDataUnitTest {


    // region isDirectlyAbove

    @Test
    fun `Test when other item is left then isDirectlyAbove is false`() {

        val itemInfo = BlueprintItemData(
            label = "Item under test",
            position = Offset(
                x = 10f,
                y = 0f,
            ),
            size = Size(
                width = 1f,
                height = 1f,
            ),
            parentConnectionConfig = WherePossible,
        )

        val itemToLeft = BlueprintItemData(
            label = "Item under test",
            position = Offset(
                x = 1f,
                y = 0f,
            ),
            size = Size(
                width = 1f,
                height = 1f,
            ),
            parentConnectionConfig = WherePossible,
        )

        assert(!(itemInfo isDirectlyAbove itemToLeft))
    }

    @Test
    fun `Test when other item is above then isDirectlyAbove is false`() {

        val itemInfo = BlueprintItemData(
            label = "Item under test",
            position = Offset(
                x = 1f,
                y = 10f,
            ),
            size = Size(
                width = 1f,
                height = 1f,
            ),
            parentConnectionConfig = WherePossible,
        )

        val itemToLeft = BlueprintItemData(
            label = "Item under test",
            position = Offset(
                x = 1f,
                y = 0f,
            ),
            size = Size(
                width = 1f,
                height = 1f,
            ),
            parentConnectionConfig = WherePossible,
        )

        assert(!(itemInfo isDirectlyAbove itemToLeft))
    }

    @Test
    fun `Test when other item is right then isDirectlyAbove is false`() {

        val itemInfo = BlueprintItemData(
            label = "Item under test",
            position = Offset(
                x = 1f,
                y = 0f,
            ),
            size = Size(
                width = 1f,
                height = 1f,
            ),
            parentConnectionConfig = WherePossible,
        )

        val itemToLeft = BlueprintItemData(
            label = "Item under test",
            position = Offset(
                x = 10f,
                y = 0f,
            ),
            size = Size(
                width = 1f,
                height = 1f,
            ),
            parentConnectionConfig = WherePossible,
        )

        assert(!(itemInfo isDirectlyAbove itemToLeft))
    }

    @Test
    fun `Test when other item is below then isDirectlyAbove is true`() {

        val itemInfo = BlueprintItemData(
            label = "Item under test",
            position = Offset(
                x = 1f,
                y = 0f,
            ),
            size = Size(
                width = 1f,
                height = 1f,
            ),
            parentConnectionConfig = WherePossible,
        )

        val itemToLeft = BlueprintItemData(
            label = "Item under test",
            position = Offset(
                x = 1f,
                y = 10f,
            ),
            size = Size(
                width = 1f,
                height = 1f,
            ),
            parentConnectionConfig = WherePossible,
        )

        assert(itemInfo isDirectlyAbove itemToLeft)
    }


    @Test
    fun `Test when other item is wider and is below then isDirectlyAbove is true`() {

        val itemInfo = BlueprintItemData(
            label = "Item under test",
            position = Offset(
                x = 4f,
                y = 0f,
            ),
            size = Size(
                width = 2f,
                height = 1f,
            ),
            parentConnectionConfig = WherePossible,
        )

        val itemToLeft = BlueprintItemData(
            label = "Item under test",
            position = Offset(
                x = 0f,
                y = 10f,
            ),
            size = Size(
                width = 10f,
                height = 1f,
            ),
            parentConnectionConfig = WherePossible,
        )

        assert(itemInfo isDirectlyAbove itemToLeft)
    }

    @Test
    fun `Test when other item is thinner and is below then isDirectlyAbove is true`() {

        val itemInfo = BlueprintItemData(
            label = "Item under test",
            position = Offset(
                x = 0f,
                y = 0f,
            ),
            size = Size(
                width = 10f,
                height = 1f,
            ),
            parentConnectionConfig = WherePossible,
        )

        val itemToLeft = BlueprintItemData(
            label = "Item under test",
            position = Offset(
                x = 4f,
                y = 10f,
            ),
            size = Size(
                width = 2f,
                height = 1f,
            ),
            parentConnectionConfig = WherePossible,
        )

        assert(itemInfo isDirectlyAbove itemToLeft)
    }

    // endregion

    // region isDirectlyLeftOf

    @Test
    fun `Test when other item is left then isDirectlyLeftOf is false`() {

        val itemInfo = BlueprintItemData(
            label = "Item under test",
            position = Offset(
                x = 10f,
                y = 0f,
            ),
            size = Size(
                width = 1f,
                height = 1f,
            ),
            parentConnectionConfig = WherePossible,
        )

        val itemToLeft = BlueprintItemData(
            label = "Item under test",
            position = Offset(
                x = 0f,
                y = 0f,
            ),
            size = Size(
                width = 1f,
                height = 1f,
            ),
            parentConnectionConfig = WherePossible,
        )

        assert(!(itemInfo isDirectlyLeftOf itemToLeft))
    }

    @Test
    fun `Test when other item is above then isDirectlyLeftOf is false`() {

        val itemInfo = BlueprintItemData(
            label = "Item under test",
            position = Offset(
                x = 1f,
                y = 10f,
            ),
            size = Size(
                width = 1f,
                height = 1f,
            ),
            parentConnectionConfig = WherePossible,
        )

        val itemToLeft = BlueprintItemData(
            label = "Item under test",
            position = Offset(
                x = 1f,
                y = 0f,
            ),
            size = Size(
                width = 1f,
                height = 1f,
            ),
            parentConnectionConfig = WherePossible,
        )

        assert(!(itemInfo isDirectlyLeftOf itemToLeft))
    }

    @Test
    fun `Test when other item is right then isDirectlyLeftOf is true`() {

        val itemInfo = BlueprintItemData(
            label = "Item under test",
            position = Offset(
                x = 1f,
                y = 0f,
            ),
            size = Size(
                width = 1f,
                height = 1f,
            ),
            parentConnectionConfig = WherePossible,
        )

        val itemToLeft = BlueprintItemData(
            label = "Item under test",
            position = Offset(
                x = 10f,
                y = 0f,
            ),
            size = Size(
                width = 1f,
                height = 1f,
            ),
            parentConnectionConfig = WherePossible,
        )

        assert(itemInfo isDirectlyLeftOf itemToLeft)
    }

    @Test
    fun `Test when other item is below then isDirectlyLeftOf is false`() {

        val itemInfo = BlueprintItemData(
            label = "Item under test",
            position = Offset(
                x = 1f,
                y = 0f,
            ),
            size = Size(
                width = 1f,
                height = 1f,
            ),
            parentConnectionConfig = WherePossible,
        )

        val itemToLeft = BlueprintItemData(
            label = "Item under test",
            position = Offset(
                x = 1f,
                y = 10f,
            ),
            size = Size(
                width = 1f,
                height = 1f,
            ),
            parentConnectionConfig = WherePossible,
        )

        assert(!(itemInfo isDirectlyLeftOf itemToLeft))
    }


    @Test
    fun `Test when other item is taller and is right then isDirectlyLeftOf is true`() {

        val itemInfo = BlueprintItemData(
            label = "Item under test",
            position = Offset(
                x = 4f,
                y = 1f,
            ),
            size = Size(
                width = 2f,
                height = 2f,
            ),
            parentConnectionConfig = WherePossible,
        )

        val itemToLeft = BlueprintItemData(
            label = "Item under test",
            position = Offset(
                x = 10f,
                y = 0f,
            ),
            size = Size(
                width = 1f,
                height = 4f,
            ),
            parentConnectionConfig = WherePossible,
        )

        assert(itemInfo isDirectlyLeftOf itemToLeft)
    }

    @Test
    fun `Test when other item is shorter and is right then isDirectlyLeftOf is true`() {

        val itemInfo = BlueprintItemData(
            label = "Item under test",
            position = Offset(
                x = 0f,
                y = 0f,
            ),
            size = Size(
                width = 1f,
                height = 6f,
            ),
            parentConnectionConfig = WherePossible,
        )

        val itemToLeft = BlueprintItemData(
            label = "Item under test",
            position = Offset(
                x = 4f,
                y = 2f,
            ),
            size = Size(
                width = 1f,
                height = 2f,
            ),
            parentConnectionConfig = WherePossible,
        )

        assert(itemInfo isDirectlyLeftOf itemToLeft)
    }

    // endregion

    // region isDirectlyBelow

    @Test
    fun `Test when other item is left then isDirectlyBelow is false`() {

        val itemInfo = BlueprintItemData(
            label = "Item under test",
            position = Offset(
                x = 10f,
                y = 0f,
            ),
            size = Size(
                width = 1f,
                height = 1f,
            ),
            parentConnectionConfig = WherePossible,
        )

        val itemToLeft = BlueprintItemData(
            label = "Item under test",
            position = Offset(
                x = 1f,
                y = 0f,
            ),
            size = Size(
                width = 1f,
                height = 1f,
            ),
            parentConnectionConfig = WherePossible,
        )

        assert(!(itemInfo isDirectlyBelow itemToLeft))
    }

    @Test
    fun `Test when other item is above then isDirectlyBelow is true`() {

        val itemInfo = BlueprintItemData(
            label = "Item under test",
            position = Offset(
                x = 1f,
                y = 10f,
            ),
            size = Size(
                width = 1f,
                height = 1f,
            ),
            parentConnectionConfig = WherePossible,
        )

        val itemToLeft = BlueprintItemData(
            label = "Item under test",
            position = Offset(
                x = 1f,
                y = 0f,
            ),
            size = Size(
                width = 1f,
                height = 1f,
            ),
            parentConnectionConfig = WherePossible,
        )

        assert(itemInfo isDirectlyBelow itemToLeft)
    }

    @Test
    fun `Test when other item is right then isDirectlyBelow is false`() {

        val itemInfo = BlueprintItemData(
            label = "Item under test",
            position = Offset(
                x = 1f,
                y = 0f,
            ),
            size = Size(
                width = 1f,
                height = 1f,
            ),
            parentConnectionConfig = WherePossible,
        )

        val itemToLeft = BlueprintItemData(
            label = "Item under test",
            position = Offset(
                x = 10f,
                y = 0f,
            ),
            size = Size(
                width = 1f,
                height = 1f,
            ),
            parentConnectionConfig = WherePossible,
        )

        assert(!(itemInfo isDirectlyBelow itemToLeft))
    }

    @Test
    fun `Test when other item is below then isDirectlyBelow is false`() {

        val itemInfo = BlueprintItemData(
            label = "Item under test",
            position = Offset(
                x = 1f,
                y = 0f,
            ),
            size = Size(
                width = 1f,
                height = 1f,
            ),
            parentConnectionConfig = WherePossible,
        )

        val itemToLeft = BlueprintItemData(
            label = "Item under test",
            position = Offset(
                x = 1f,
                y = 10f,
            ),
            size = Size(
                width = 1f,
                height = 1f,
            ),
            parentConnectionConfig = WherePossible,
        )

        assert(!(itemInfo isDirectlyBelow itemToLeft))
    }


    @Test
    fun `Test when other item is wider and is above then isDirectlyBelow is true`() {

        val itemInfo = BlueprintItemData(
            label = "Item under test",
            position = Offset(
                x = 3f,
                y = 10f,
            ),
            size = Size(
                width = 4f,
                height = 1f,
            ),
            parentConnectionConfig = WherePossible,
        )

        val itemToLeft = BlueprintItemData(
            label = "Item under test",
            position = Offset(
                x = 0f,
                y = 0f,
            ),
            size = Size(
                width = 10f,
                height = 1f,
            ),
            parentConnectionConfig = WherePossible,
        )

        assert(itemInfo isDirectlyBelow itemToLeft)
    }

    @Test
    fun `Test when other item is thinner and is above then isDirectlyBelow is true`() {

        val itemInfo = BlueprintItemData(
            label = "Item under test",
            position = Offset(
                x = 0f,
                y = 10f,
            ),
            size = Size(
                width = 10f,
                height = 1f,
            ),
            parentConnectionConfig = WherePossible,
        )

        val itemToLeft = BlueprintItemData(
            label = "Item under test",
            position = Offset(
                x = 4f,
                y = 0f,
            ),
            size = Size(
                width = 2f,
                height = 1f,
            ),
            parentConnectionConfig = WherePossible,
        )

        assert(itemInfo isDirectlyBelow itemToLeft)
    }

    // endregion

    // region isDirectlyRightOf

    @Test
    fun `Test when other item is left then isDirectlyRightOf is true`() {

        val itemInfo = BlueprintItemData(
            label = "Item under test",
            position = Offset(
                x = 10f,
                y = 0f,
            ),
            size = Size(
                width = 1f,
                height = 1f,
            ),
            parentConnectionConfig = WherePossible,
        )

        val itemToLeft = BlueprintItemData(
            label = "Item under test",
            position = Offset(
                x = 0f,
                y = 0f,
            ),
            size = Size(
                width = 1f,
                height = 1f,
            ),
            parentConnectionConfig = WherePossible,
        )

        assert(itemInfo isDirectlyRightOf itemToLeft)
    }

    @Test
    fun `Test when other item is above then isDirectlyRightOf is false`() {

        val itemInfo = BlueprintItemData(
            label = "Item under test",
            position = Offset(
                x = 1f,
                y = 10f,
            ),
            size = Size(
                width = 1f,
                height = 1f,
            ),
            parentConnectionConfig = WherePossible,
        )

        val itemToLeft = BlueprintItemData(
            label = "Item under test",
            position = Offset(
                x = 1f,
                y = 0f,
            ),
            size = Size(
                width = 1f,
                height = 1f,
            ),
            parentConnectionConfig = WherePossible,
        )

        assert(!(itemInfo isDirectlyRightOf itemToLeft))
    }

    @Test
    fun `Test when other item is right then isDirectlyRightOf is false`() {

        val itemInfo = BlueprintItemData(
            label = "Item under test",
            position = Offset(
                x = 1f,
                y = 0f,
            ),
            size = Size(
                width = 1f,
                height = 1f,
            ),
            parentConnectionConfig = WherePossible,
        )

        val itemToLeft = BlueprintItemData(
            label = "Item under test",
            position = Offset(
                x = 10f,
                y = 0f,
            ),
            size = Size(
                width = 1f,
                height = 1f,
            ),
            parentConnectionConfig = WherePossible,
        )

        assert(!(itemInfo isDirectlyRightOf itemToLeft))
    }

    @Test
    fun `Test when other item is below then isDirectlyRightOf is false`() {

        val itemInfo = BlueprintItemData(
            label = "Item under test",
            position = Offset(
                x = 1f,
                y = 0f,
            ),
            size = Size(
                width = 1f,
                height = 1f,
            ),
            parentConnectionConfig = WherePossible,
        )

        val itemToLeft = BlueprintItemData(
            label = "Item under test",
            position = Offset(
                x = 1f,
                y = 10f,
            ),
            size = Size(
                width = 1f,
                height = 1f,
            ),
            parentConnectionConfig = WherePossible,
        )

        assert(!(itemInfo isDirectlyRightOf itemToLeft))
    }


    @Test
    fun `Test when other item is taller and is left then isDirectlyRightOf is true`() {

        val itemInfo = BlueprintItemData(
            label = "Item under test",
            position = Offset(
                x = 10f,
                y = 1f,
            ),
            size = Size(
                width = 2f,
                height = 2f,
            ),
            parentConnectionConfig = WherePossible,
        )

        val itemToLeft = BlueprintItemData(
            label = "Item under test",
            position = Offset(
                x = 4f,
                y = 0f,
            ),
            size = Size(
                width = 1f,
                height = 4f,
            ),
            parentConnectionConfig = WherePossible,
        )

        assert(itemInfo isDirectlyRightOf itemToLeft)
    }

    @Test
    fun `Test when other item is shorter and is left then isDirectlyRightOf is true`() {

        val itemInfo = BlueprintItemData(
            label = "Item under test",
            position = Offset(
                x = 4f,
                y = 0f,
            ),
            size = Size(
                width = 1f,
                height = 10f,
            ),
            parentConnectionConfig = WherePossible,
        )

        val itemToLeft = BlueprintItemData(
            label = "Item under test",
            position = Offset(
                x = 0f,
                y = 1f,
            ),
            size = Size(
                width = 1f,
                height = 6f,
            ),
            parentConnectionConfig = WherePossible,
        )

        assert(itemInfo isDirectlyRightOf itemToLeft)
    }

    // endregion
}