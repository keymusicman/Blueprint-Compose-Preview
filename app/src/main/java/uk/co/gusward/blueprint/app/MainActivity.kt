package uk.co.gusward.blueprint.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import uk.co.gusward.example.ExampleFeatureLayoutActiveBlueprintPreview

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExampleFeatureLayoutActiveBlueprintPreview()
        }
    }
}
