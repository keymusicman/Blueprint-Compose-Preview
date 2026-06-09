# Blueprint Preview

<p align="center">
  <img src="BlueprintLogo.png" width="800" alt="Blueprint Preview Logo">
</p>

## The goods

Blueprint Preview is a dev tool for Jetpack Compose that shows you a "blueprint" overlay of your composables in the Android Studio Preview panel. It passively measures the components in your layout and renders dimensions and spacing just like an architectural blueprint.

```kotlin
dependencies {
    debugImplementation("uk.co.gusward:blueprint-compose-preview:1.0.0")
    releaseImplementation("uk.co.gusward:blueprint-compose-preview-no-op:1.0.0")
}
```

```kotlin
@Preview
@Composable
fun MyComponentPreview() {
    BlueprintPreview { // <-- that's all!
        MyComponent()
    }
}
```

## The ramble

Hey!

Thought it would be nice to add some quick background on the roots of this little project.

I started the idea a couple years ago, and wrote all of the blueprint measurement and rendering logic by hand before LLM agents became part of my daily dev work.

Originally it worked by the user / dev wrapping every component with a `BlueprintItem {}`, which allowed the grid to easily find and render it. This worked and it looked great! But I knew the extra dev friction would make it annoying to use. I also knew the tree parsing logic to make it passive would take a while to perfect, and to be honest it sounded boring, so I parked it.

Recently with the help of Gemini I revived the project, enabling me to very quickly add the dense tree parsing logic to make the blueprint a completely passive one-liner.

So while the majority of the project was hand made, this final push has been massively boosted by AI, and completed in just a couple of days 🚀

Hope you find it useful! (the rest of this readme was written by AI haha)

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
    releaseImplementation("uk.co.gusward:blueprint-compose-preview-no-op:1.0.0")
}
```

*Note: It is recommended to use `debugImplementation` as this is a development-only tool.*

## Usage

Simply wrap your Composable in a `BlueprintPreview` block inside your `@Preview` function:

```kotlin
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
