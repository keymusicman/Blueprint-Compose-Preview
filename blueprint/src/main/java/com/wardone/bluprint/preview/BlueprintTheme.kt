package com.wardone.bluprint.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.wardone.bluprint.constants.SemanticColors

@Composable
fun BlueprintTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            background = Color.Transparent,
            onBackground = SemanticColors.BlueprintAccent,
        ),
        content = {
            Box(
                modifier = Modifier.background(SemanticColors.BlueprintBackground)
            ) {
                ProvideTextStyle(
                    value = TextStyle(SemanticColors.BlueprintAccent)
                ) {
                    content()
                }
            }
        }
    )
}
