package uk.co.gusward.bluprint.preview

import android.view.View
import android.view.ViewTreeObserver
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.ViewRootForTest
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uk.co.gusward.bluprint.constants.SemanticColors
import uk.co.gusward.bluprint.grid.BlueprintGrid
import uk.co.gusward.bluprint.items.BlueprintItemData
import uk.co.gusward.bluprint.items.WherePossible
import java.text.DecimalFormat

import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.runtime.currentCompositeKeyHashCode

// THE SURVIVOR CACHE: IMMUNE TO LAYOUTLIB'S RE-COMPOSITION WIPES
private var staticBlueprintCache: MutableMap<Long, Map<String, BlueprintItemData>> = mutableMapOf()

@Composable
fun PassiveBlueprintPreview(
    backgroundAlpha: Float = 1f,
    contentAlpha: Float = 1f,
    content: @Composable () -> Unit
) {
    BlueprintTheme(backgroundAlpha = backgroundAlpha) {
        val compositeKey = currentCompositeKeyHashCode
        // Initialize state directly from the static cache to bypass Layoutlib zoom wipes
        var blueprintItemDataState by remember {
            mutableStateOf(staticBlueprintCache[compositeKey] ?: emptyMap())
        }
        val view = LocalView.current
        val isInspectionMode = LocalInspectionMode.current

        // This effect acts as a debounce/recovery mechanism.
        DisposableEffect(view) {
            val listener = ViewTreeObserver.OnGlobalLayoutListener {
                if (view.width > 0 && view.height > 0) {
                    try {
                        val newMap = extractBlueprintItemsFromSemantics(view)
                        if (newMap.isNotEmpty() && newMap != blueprintItemDataState) {
                            blueprintItemDataState = newMap
                            staticBlueprintCache[compositeKey] = newMap // Anchor to cache
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("PassiveBlueprint", "Recovery extraction failed", e)
                    }
                }
            }
            view.viewTreeObserver.addOnGlobalLayoutListener(listener)
            onDispose {
                view.viewTreeObserver.removeOnGlobalLayoutListener(listener)
            }
        }

        // CRITICAL IDE HACK: Forcing a synchronous semantics extraction directly during the Compose phase.
        if (isInspectionMode) {
            try {
                val immediateMap = extractBlueprintItemsFromSemantics(view)
                if (immediateMap.isNotEmpty() && immediateMap != blueprintItemDataState) {
                    blueprintItemDataState = immediateMap
                    staticBlueprintCache[compositeKey] = immediateMap // Anchor to cache
                }
            } catch (e: Exception) {
                // Ignore
            }
        }

        // Reset cache if we're in diagnostic mode or just to be safe during these changes
        // staticBlueprintCache.clear() 

        Box(
            modifier = Modifier
                .fillMaxSize()
                .onGloballyPositioned { layoutCoordinates ->
                    if (layoutCoordinates.size.width > 0 && layoutCoordinates.size.height > 0) {
                        val newMap = extractBlueprintItemsFromSemantics(view)
                        if (newMap.isNotEmpty() && newMap != blueprintItemDataState) {
                            blueprintItemDataState = newMap
                            staticBlueprintCache[compositeKey] = newMap
                        }
                    }
                }
        ) {
            // 1. Draw the actual content first, faded
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(contentAlpha)
            ) {
                content()
            }

            // 2. Draw the Blueprint Grid and Overlay on top
            BlueprintGrid(
                gridSize = 24.dp,
                blueprintItems = blueprintItemDataState,
                alpha = backgroundAlpha
            ) {
                if (blueprintItemDataState.isEmpty()) {
                    // Fallback state...
                    DisposableEffect(view) {
                        val listener = ViewTreeObserver.OnPreDrawListener {
                            try {
                                val recoveredMap = extractBlueprintItemsFromSemantics(view)
                                if (recoveredMap.isNotEmpty() && recoveredMap != blueprintItemDataState) {
                                    blueprintItemDataState = recoveredMap
                                    staticBlueprintCache[compositeKey] = recoveredMap
                                }
                            } catch (e: Exception) {}
                            true
                        }
                        view.viewTreeObserver.addOnPreDrawListener(listener)
                        onDispose { view.viewTreeObserver.removeOnPreDrawListener(listener) }
                    }

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Assemble to see Blueprint",
                            color = Color.White.copy(alpha = backgroundAlpha * 0.5f),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .semantics { testTag = "blueprint_fallback_text" }
                                .background(SemanticColors.BlueprintBackground.copy(alpha = backgroundAlpha * 0.8f))
                                .border(1.dp, Color.White.copy(alpha = backgroundAlpha * 0.5f))
                                .padding(16.dp)
                        )
                    }
                } else {
                    // Draw the visual boxes
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .semantics { 
                                testTag = "blueprint_internal_overlay" 
                            }
                    ) {
                        blueprintItemDataState.values.forEach { item ->
                            PassiveBlueprintItemOverlay(item, backgroundAlpha)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PassiveBlueprintItemOverlay(itemData: BlueprintItemData, backgroundAlpha: Float) {
    val decimalFormat = remember { DecimalFormat("0") }

    Box(
        modifier = Modifier
            .offset { IntOffset(itemData.position.x.toInt(), itemData.position.y.toInt()) }
            .size(
                width = LocalDensity.current.run { itemData.size.width.toDp() },
                height = LocalDensity.current.run { itemData.size.height.toDp() }
            )
            .clearAndSetSemantics { } // Hide from semantics tree to prevent infinite loops!
            .border(width = 2.dp, color = Color.White.copy(alpha = backgroundAlpha))
            .background(SemanticColors.BlueprintBackground.copy(alpha = backgroundAlpha))
    ) {
        // Draw repeated 45 degree lines
        Canvas(modifier = Modifier.fillMaxSize()) {
            val path = Path()
            path.moveTo(size.width, 0f)
            path.lineTo(size.width, size.height)
            path.lineTo(0f, size.height)
            path.lineTo(0f, 0f)

            clipPath(path = path, clipOp = ClipOp.Intersect) {
                val spacing = size.width / 10
                var start = spacing
                do {
                    drawLine(
                        start = Offset(x = 0f, y = start),
                        end = Offset(x = start, y = 0f),
                        color = Color.White.copy(alpha = backgroundAlpha * 0.5f),
                        strokeWidth = 2f,
                    )
                    start += spacing
                } while (start < size.width + size.height)
            }
        }

        // Draw label
        val itemSize = itemData.size
        // Tiered scaling: reduce vertical padding first, then scale font
        val density = LocalDensity.current
        val itemDpHeight = density.run { itemSize.height.toDp() }
        val itemDpWidth = density.run { itemSize.width.toDp() }

        var fontSize = 12.sp
        var vPadding = 2.dp
        var hPadding = 2.dp
        
        val fullHeightNeeded = 26.dp
        val textOnlyHeight = 18.dp
        val fullWidthNeeded = 60.dp

        // 1. Height-based scaling (Linear Progress)
        if (itemDpHeight < fullHeightNeeded) {
            if (itemDpHeight >= textOnlyHeight) {
                // Stage 1: Linearly reduce padding from 2dp to 0dp
                val progress = (itemDpHeight - textOnlyHeight) / (fullHeightNeeded - textOnlyHeight)
                vPadding = (2 * progress).dp
            } else {
                // Stage 2: Padding is gone, scale font
                vPadding = 0.dp
                val heightScale = (itemDpHeight / textOnlyHeight).coerceAtLeast(0.4f)
                fontSize = (12 * heightScale).sp
                hPadding = (2 * heightScale).dp
            }
        }

        // 2. Width-based scaling (Simple)
        if (itemDpWidth < fullWidthNeeded) {
            val widthScale = (itemDpWidth / fullWidthNeeded).coerceAtLeast(0.4f)
            val currentFontValue = fontSize.value
            val widthScaledFontValue = 12 * widthScale
            if (widthScaledFontValue < currentFontValue) {
                fontSize = widthScaledFontValue.sp
                hPadding = (2 * (widthScaledFontValue / 12f)).dp
            }
        }

        if (itemSize.width > (itemSize.height * 2)) {
            Row(
                modifier = Modifier
                    .background(SemanticColors.BlueprintBackground.copy(alpha = backgroundAlpha))
                    .border(1.dp, Color.White.copy(alpha = backgroundAlpha * 0.7f))
                    .padding(horizontal = hPadding, vertical = vPadding)
                    .align(Alignment.Center),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    fontSize = fontSize,
                    color = Color.White.copy(alpha = backgroundAlpha),
                    fontWeight = FontWeight.Medium,
                    text = density.run {
                        val width = decimalFormat.format(itemSize.width.toDp().value)
                        val height = decimalFormat.format(itemSize.height.toDp().value)
                        "${width}x${height}"
                    },
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .background(SemanticColors.BlueprintBackground.copy(alpha = backgroundAlpha))
                    .border(1.dp, Color.White.copy(alpha = backgroundAlpha * 0.7f))
                    .padding(horizontal = hPadding, vertical = vPadding)
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    fontSize = fontSize,
                    color = Color.White.copy(alpha = backgroundAlpha),
                    fontWeight = FontWeight.Medium,
                    text = density.run {
                        val width = decimalFormat.format(itemSize.width.toDp().value)
                        val height = decimalFormat.format(itemSize.height.toDp().value)
                        "${width}x${height}"
                    },
                )
            }
        }
    }
}

private fun findAndroidComposeView(view: View): View? {
    if (view.javaClass.name.contains("AndroidComposeView")) {
        return view
    }
    if (view is android.view.ViewGroup) {
        for (i in 0 until view.childCount) {
            val found = findAndroidComposeView(view.getChildAt(i))
            if (found != null) return found
        }
    }
    return null
}

// A thread-local or temporary set to track nodes that should be ignored in the current traversal
private val suppressedNodes = mutableSetOf<Int>()

fun extractBlueprintItemsFromSemantics(view: View): Map<String, BlueprintItemData> {
    val items = mutableMapOf<String, BlueprintItemData>()
    suppressedNodes.clear() 

    var composeView: ViewRootForTest? = null
    var currentView: View? = view
    while (currentView != null) {
        if (currentView is ViewRootForTest) {
            composeView = currentView
            break
        }
        currentView = currentView.parent as? View
    }

    if (composeView != null) {
        try {
            val semanticsOwner = composeView.semanticsOwner
            // Prefer unmerged tree to see all blueprint items
            val rootNode = try {
                val unmergedProperty = semanticsOwner.javaClass.getDeclaredMethod("getUnmergedRootSemanticsNode")
                unmergedProperty.isAccessible = true
                unmergedProperty.invoke(semanticsOwner) as androidx.compose.ui.semantics.SemanticsNode
            } catch (e: Exception) {
                semanticsOwner.rootSemanticsNode
            }
            traverseSemanticsNode(rootNode, items)
            return items
        } catch (e: Exception) {
            android.util.Log.e("PassiveBlueprint", "Direct extraction failed", e)
        }
    } else {
        try {
            val androidComposeView = findAndroidComposeView(view)

            if (androidComposeView != null) {
                val semanticsOwnerField = androidComposeView.javaClass.getDeclaredMethod("getSemanticsOwner")
                semanticsOwnerField.isAccessible = true
                val semanticsOwner = semanticsOwnerField.invoke(androidComposeView)

                if (semanticsOwner != null) {
                    val rootNode = try {
                        val unmergedProperty = semanticsOwner.javaClass.getDeclaredMethod("getUnmergedRootSemanticsNode")
                        unmergedProperty.isAccessible = true
                        unmergedProperty.invoke(semanticsOwner) as androidx.compose.ui.semantics.SemanticsNode
                    } catch (e: Exception) {
                        val rootSemanticsNodeMethod = semanticsOwner.javaClass.getDeclaredMethod("getRootSemanticsNode")
                        rootSemanticsNodeMethod.isAccessible = true
                        rootSemanticsNodeMethod.invoke(semanticsOwner) as androidx.compose.ui.semantics.SemanticsNode
                    }
                    traverseSemanticsNode(rootNode, items)
                }
            } else {
                android.util.Log.e("PassiveBlueprint", "Could not find AndroidComposeView in hierarchy via reflection")
            }
        } catch (ex: Exception) {
            android.util.Log.e("PassiveBlueprint", "Fallback extraction failed", ex)
        }
    }
    return items
}

fun traverseSemanticsNode(node: androidx.compose.ui.semantics.SemanticsNode, items: MutableMap<String, BlueprintItemData>) {
    try {
        val id = node.id
        
        // Use layoutInfo to get TRUE physical bounds.
        val layoutInfo = node.layoutInfo
        val outerCoordinates = try {
            val getModifierInfoMethod = layoutInfo.javaClass.getMethod("getModifierInfo")
            getModifierInfoMethod.isAccessible = true
            val modifierInfoList = getModifierInfoMethod.invoke(layoutInfo) as List<*>
            if (modifierInfoList.isNotEmpty()) {
                val firstModInfo = modifierInfoList.first()!!
                val coordsMethod = firstModInfo.javaClass.getMethod("getCoordinates")
                coordsMethod.invoke(firstModInfo) as androidx.compose.ui.layout.LayoutCoordinates
            } else {
                val outerCoordMethod = layoutInfo.javaClass.getMethod("getOuterCoordinator")
                outerCoordMethod.isAccessible = true
                outerCoordMethod.invoke(layoutInfo) as androidx.compose.ui.layout.LayoutCoordinates
            }
        } catch (e: Exception) {
            try {
                val outerCoordMethod = layoutInfo.javaClass.getMethod("getOuterCoordinator")
                outerCoordMethod.isAccessible = true
                outerCoordMethod.invoke(layoutInfo) as androidx.compose.ui.layout.LayoutCoordinates
            } catch (e2: Exception) {
                layoutInfo.coordinates
            }
        }
        
        val bounds = outerCoordinates.boundsInRoot()
        
        android.util.Log.d("PassiveBlueprint", "Node $id: bounds=$bounds, layoutInfoBounds=${layoutInfo.coordinates.boundsInRoot()}, semanticBounds=${node.boundsInRoot}")

        var label = "Node $id"
        var hasExplicitLabel = false
        
        val config = node.config

        // Priority 1: TestTag (includes blueprintId) - Developers use this for explicit naming
        val testTag = config.getOrNull(SemanticsProperties.TestTag)
        if (testTag != null) {
            if (testTag == "blueprint_fallback_text" || testTag == "blueprint_internal_overlay") {
                return // PRUNE THE SUBTREE
            }
            label = testTag
            hasExplicitLabel = true
        }

        // Priority 2: Text content - Fallback for untagged items
        if (!hasExplicitLabel) {
            val textList = config.getOrNull(SemanticsProperties.Text)
            if (!textList.isNullOrEmpty()) {
                label = textList.joinToString(", ")
                if (label == "Assemble to see Blueprint") return // Ignore our own UI
                hasExplicitLabel = true
            }
        }

        // Priority 3: Content Description
        if (!hasExplicitLabel) {
            val contentDescription = config.getOrNull(SemanticsProperties.ContentDescription)
            if (!contentDescription.isNullOrEmpty()) {
                label = contentDescription.joinToString(", ")
                hasExplicitLabel = true
            }
        }

        // --- ID PREFERENCE & REDUNDANCY FILTER ---
        // If this node has an EXPLICIT TestTag AND it's a layout (not a Leaf node),
        // we check for a single labeled child to "absorb" to prevent double blueprints.
        // We MUST verify testTag != null, otherwise untagged parents might accidentally
        // inherit a child's label and suppress other children.
        if (hasExplicitLabel && testTag != null && node.children.isNotEmpty()) {
            val labeledChildren = node.children.filter { child ->
                val childConfig = child.config
                childConfig.contains(SemanticsProperties.TestTag) ||
                !childConfig.getOrNull(SemanticsProperties.Text).isNullOrEmpty() ||
                !childConfig.getOrNull(SemanticsProperties.ContentDescription).isNullOrEmpty()
            }

            if (labeledChildren.size == 1) {
                val childToSuppress = labeledChildren.first()
                suppressedNodes.add(childToSuppress.id)
            }
        }

        if (hasExplicitLabel && !suppressedNodes.contains(id) && bounds.width > 2f && bounds.height > 2f && !node.isRoot) {
            items[id.toString()] = BlueprintItemData(
                id = id.toString(),
                label = label,
                position = Offset(bounds.left, bounds.top),
                size = Size(bounds.width, bounds.height),
                parentConnectionConfig = WherePossible
            )
        }

        // ALWAYS traverse children
        val children = node.children
        for (child in children) {
            traverseSemanticsNode(child, items)
        }
    } catch (e: Exception) {
        android.util.Log.e("PassiveBlueprint", "Failed traversing node", e)
    }
}