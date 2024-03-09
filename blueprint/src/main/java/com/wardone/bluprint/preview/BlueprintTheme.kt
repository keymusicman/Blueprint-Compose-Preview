package com.wardone.bluprint.preview

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import com.wardone.bluprint.constants.SemanticColors

@Composable
fun BlueprintTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            background = SemanticColors.BlueprintBackground,
            onBackground = SemanticColors.BlueprintAccent,
        ),
        content = {
            ProvideTextStyle(
                value = TextStyle(SemanticColors.BlueprintAccent)
            ) {
                content()
            }
        }
    )
}
