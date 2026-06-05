package uk.co.gusward.example

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uk.co.gusward.bluprint.preview.PassiveBlueprintPreview
import uk.co.gusward.bluprint.preview.blueprintId

@Composable
fun ExampleComponent(
    modifier: Modifier = Modifier,
    image: @Composable () -> Unit,
    title: @Composable () -> Unit,
    body: @Composable () -> Unit,
    action: @Composable () -> Unit,
) {
    Column(
        modifier = modifier.padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(30.dp),
        ) {
            Box(
                modifier = Modifier.size(70.dp)
            ) {
                image()
            }
            title()
        }
        Box(
            modifier = Modifier.height(80.dp)
        ) {
            body()
        }
        Spacer(modifier = Modifier.weight(1f))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            horizontalArrangement = Arrangement.End
        ) {
            action()
        }
    }
}

val exampleColorScheme = darkColorScheme(
    background = Color(0xFFA3C4F3)
)

@Preview(device = "spec:width=300dp,height=300dp,dpi=440")
@Composable
fun ExampleComponentStandardPreview() {
    MaterialTheme(
        colorScheme = exampleColorScheme,
    ) {
        ExampleComponent(
            modifier = Modifier
                .size(300.dp)
                .background(
                    color = MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(10.dp),
                ),
            image = {
                Image(
                    modifier = Modifier
                        .shadow(
                            elevation = 8.dp,
                            shape = CircleShape
                        )
                        .clip(CircleShape)
                        .border(
                            width = 2.dp,
                            color = Color.White,
                            shape = CircleShape,
                        ),
                    painter = painterResource(id = R.drawable.me),
                    contentDescription = "Image"
                )
            },
            title = {
                    Text(
                        modifier = Modifier
                            .background(
                                color = Color.White,
                                shape = RoundedCornerShape(5.dp),
                            )
                            .padding(5.dp)
                            .fillMaxWidth(),
                        text = "Example Component",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                    )
            },
            body = {
                Text(
                    modifier = Modifier
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(5.dp),
                        )
                        .padding(5.dp),
                    text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
                    style = TextStyle(
                        fontSize = 12.sp
                    ),
                    overflow = TextOverflow.Ellipsis
                )
            },
            action = {
                IconButton(
                    modifier = Modifier
                        .blueprintId("2")
                        .shadow(
                            elevation = 8.dp,
                            shape = CircleShape
                        )
                        .background(
                            color = Color(0xFFB9FBC0),
                            shape = CircleShape,
                        ),
                    onClick = { }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_thumb_up_off_alt_24),
                        contentDescription = "Action",
                        tint = Color.White
                    )
                }
            }
        )
    }
}

@Preview(device = "spec:width=300dp,height=300dp,dpi=440")
@Composable
fun ExampleComponentBlueprintPreview() {
    PassiveBlueprintPreview {
        ExampleComponentStandardPreview()
    }
}