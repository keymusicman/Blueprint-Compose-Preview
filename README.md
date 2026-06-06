# Blueprint Preview

<p align="center">
  <img src="BlueprintLogo.png" width="800" alt="Blueprint Preview Logo">
</p>

## The goods

Blueprint Preview is a dev tool for Jetpack Compose that shows you a "blueprint" overlay of your composables in the Android Studio Preview panel. It passively measures the components in your layout and renders dimensions and spacing just like an architectural blueprint.

```kotlin
dependencies {
    debugImplementation("uk.co.gusward:blueprint-compose-preview:1.0.0")
}
```

```kotlin
import androidx.compose.ui.tooling.preview.Preview
import uk.co.gusward.bluprint.preview.BlueprintPreview

@Preview
@Composable
fun MyComponentPreview() {
    BlueprintPreview { // <-- that's all!
        MyComponent()
    }
}
```

## The ramble

Hey, welcome to my project! ☀️

As the world is becoming more and more AI driven, I thought it would be nice to add some background and a semi disclaimer for the boost AI gave me to finish it.

I started this little project a couple years ago, before AI was a good enough to find its way into my daily dev work, and it was great! without any help from the machine I managed to get the blueprint preview overlay measuring and looking exactly as it does now 😎

It was never released back then partly because I didn't have time, but mainly because of practicality. What I originally built used an active measurement method, so you had to wrap every individual component in your composable with a `BlueprintItem { }`, which the grid then used to take its measurement.

Fast forward to now, my daily dev workflow is almost 100% AI driven and output just mind blowing, I realised with the help of AI this project could be revived and improved to make something could actually be useful, so here it is!

The blueprint is now completely passive, so with a one line wrapper around your component you get a fully measured blueprint in Android Studio which you can compare with designs at a glance. (for more control you can still add blueprintId modifier, which is just a fancy name for test tag).

the rest of this readme was written by AI haha

\- Gus

## The rest

<p align="center">
  <img src="ExampleComponentBlueprint.png" width="800" alt="Example Component Blueprint">
</p>

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
