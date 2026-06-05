package uk.co.gusward.bluprint.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.LineHeightStyle
import uk.co.gusward.bluprint.constants.SemanticColors

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
            Box(
                modifier = Modifier.background(SemanticColors.BlueprintBackground)
            ) {
                ProvideTextStyle(
                    value = TextStyle(
                        color = SemanticColors.BlueprintAccent,
                        platformStyle = PlatformTextStyle(
                            includeFontPadding = false
                        ),
                        lineHeightStyle = LineHeightStyle(
                            alignment = LineHeightStyle.Alignment.Center,
                            trim = LineHeightStyle.Trim.Both
                        )
                    )
                ) {
                    content()
                }
            }
        }
    )
}
