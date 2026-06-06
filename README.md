# Blueprint Preview

<p align="center">
  <img src="BlueprintLogo.png" width="800" alt="Blueprint Preview Logo">
</p>

Blueprint Preview is a developer productivity library for Jetpack Compose that allows you to see a "blueprint" overlay of your composables directly in the Android Studio Preview. It passively measures the components in your layout and renders dimensions, spacing, and alignment guides.

## Features

- **📏 Passive Measurement**: No changes required to your existing production code.
- **🗺️ Visual Overlay**: See the exact pixel (or DP) dimensions of your widgets.
- **🎯 Precision Alignment**: View spacing between components and parent boundaries.
- **🏷️ Smart Labeling**: Automatically extracts labels from `Text`, `ContentDescription`, or `testTag`.
- **🔌 Easy Integration**: Just wrap your existing `@Preview` content.
- **🎨 Configurable**: Adjust transparency and background grid settings.

## Installation

Add the dependency to your `build.gradle.kts` file:

```kotlin
dependencies {
    debugImplementation("uk.co.gusward:blueprint-compose-preview:1.0.0")
}
```

*Note: It is recommended to use `debugImplementation` as this is a development-only tool.*

## Usage

Simply wrap your Composable in a `BlueprintPreview` block inside your `@Preview` function:

```kotlin
import androidx.compose.ui.tooling.preview.Preview
import uk.co.gusward.bluprint.preview.BlueprintPreview

@Preview
@Composable
fun MyComponentPreview() {
    BlueprintPreview {
        MyComponent()
    }
}
```

### Explicit Naming with `blueprintId`

By default, the library tries to identify components using their text or content descriptions. For more complex layouts or to give a specific name to a container, you can use the `blueprintId` modifier:

```kotlin
import uk.co.gusward.bluprint.preview.blueprintId

@Composable
fun MyComplexLayout() {
    Column(modifier = Modifier.blueprintId("MainContainer")) {
        // ...
    }
}
```

### Configuration Options

The `BlueprintPreview` composable accepts parameters to tune the visual appearance:

| Parameter | Type | Default | Description |
| :--- | :--- | :--- | :--- |
| `backgroundAlpha` | `Float` | `1.0f` | Transparency of the blueprint grid and labels. |
| `contentAlpha` | `Float` | `1.0f` | Transparency of your actual content underneath the blueprint. |

Example with transparency:

```kotlin
@Preview
@Composable
fun GhostlyPreview() {
    BlueprintPreview(
        backgroundAlpha = 0.6f,
        contentAlpha = 0.3f
    ) {
        MyComponent()
    }
}
```

## How it Works

Blueprint Preview uses the Compose Semantics tree to discover and measure layout nodes. It injects a custom `Canvas` overlay that renders the blueprint aesthetics on top of your content. It includes specific optimizations to handle Android Studio's `Layoutlib` re-composition cycles, ensuring the blueprint remains visible even when zooming or interacting with the preview.

---

<p align="center">
  Made with ❤️ for the Android Community
</p>
