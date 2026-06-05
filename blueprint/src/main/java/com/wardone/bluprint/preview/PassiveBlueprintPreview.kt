package com.wardone.bluprint.preview

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
import com.wardone.bluprint.constants.SemanticColors
import com.wardone.bluprint.grid.BlueprintGrid
import com.wardone.bluprint.items.BlueprintItemData
import com.wardone.bluprint.items.WherePossible
import java.text.DecimalFormat

import androidx.compose.runtime.currentCompositeKeyHash
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag

// THE SURVIVOR CACHE: IMMUNE TO LAYOUTLIB'S RE-COMPOSITION WIPES
private var staticBlueprintCache: MutableMap<Int, Map<String, BlueprintItemData>> = mutableMapOf()

@Composable
fun PassiveBlueprintPreview(
    content: @Composable () -> Unit
) {
    BlueprintTheme {
        val compositeKey = currentCompositeKeyHash
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
                    .alpha(0.5f)
            ) {
                content()
            }

            // 2. Draw the Blueprint Grid and Overlay on top
            BlueprintGrid(
                gridSize = 24.dp,
                blueprintItems = blueprintItemDataState
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
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .semantics { testTag = "blueprint_fallback_text" }
                                .background(SemanticColors.BlueprintBackground.copy(alpha = 0.8f))
                                .border(1.dp, Color.White)
                                .padding(16.dp)
                        )
                    }
                } else {
                    // Draw the visual boxes
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .semantics { testTag = "blueprint_internal_overlay" }
                    ) {
                        blueprintItemDataState.values.forEach { item ->
                            PassiveBlueprintItemOverlay(item)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PassiveBlueprintItemOverlay(itemData: BlueprintItemData) {
    val decimalFormat = remember { DecimalFormat("0") }

    Box(
        modifier = Modifier
            .offset { IntOffset(itemData.position.x.toInt(), itemData.position.y.toInt()) }
            .size(
                width = LocalDensity.current.run { itemData.size.width.toDp() },
                height = LocalDensity.current.run { itemData.size.height.toDp() }
            )
            .clearAndSetSemantics { } // Hide from semantics tree to prevent infinite loops!
            .border(width = 2.dp, color = Color.White)
            .background(SemanticColors.BlueprintBackground.copy(alpha = 0.8f))
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
                        color = Color.White,
                        strokeWidth = 2f,
                    )
                    start += spacing
                } while (start < size.width + size.height)
            }
        }

        // Draw label
        val itemSize = itemData.size
        if (itemSize.width > (itemSize.height * 2)) {
            Row(
                modifier = Modifier
                    .background(SemanticColors.BlueprintBackground)
                    .border(1.dp, Color.White.copy(alpha = 0.7f))
                    .padding(2.dp)
                    .align(Alignment.Center),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    color = Color.White,
                    text = itemData.label,
                )
                Text(
                    fontSize = 12.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    text = LocalDensity.current.run {
                        val width = decimalFormat.format(itemSize.width.toDp().value)
                        val height = decimalFormat.format(itemSize.height.toDp().value)
                        "${width}x${height}"
                    },
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .background(SemanticColors.BlueprintBackground)
                    .border(1.dp, Color.White.copy(alpha = 0.7f))
                    .padding(2.dp)
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    color = Color.White,
                    text = itemData.label,
                )
                Text(
                    fontSize = 12.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    text = LocalDensity.current.run {
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

fun extractBlueprintItemsFromSemantics(view: View): Map<String, BlueprintItemData> {
    val items = mutableMapOf<String, BlueprintItemData>()

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
        val bounds = node.boundsInRoot

        var label = "Node $id"
        var hasExplicitLabel = false
        
        val config = node.config
        
        // Priority 1: TestTag (includes blueprintId)
        val testTag = config.getOrNull(SemanticsProperties.TestTag)
        if (testTag != null) {
            if (testTag == "blueprint_fallback_text" || testTag == "blueprint_internal_overlay") {
                return // PRUNE THE SUBTREE: Skip our own internal UI completely
            } else {
                label = testTag
                hasExplicitLabel = true
            }
        }

        // Priority 2: Text content
        if (!hasExplicitLabel) {
            val textList = config.getOrNull(SemanticsProperties.Text)
            if (!textList.isNullOrEmpty()) {
                label = textList.joinToString(", ")
                // CRITICAL: We MUST NOT detect our own label boxes if they were somehow missed by the tag prune
                if (label == "Assemble to see Blueprint") return
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

        // Only include nodes with explicit labels and reasonable size to filter noise
        if (hasExplicitLabel && bounds.width > 2f && bounds.height > 2f && !node.isRoot) {
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