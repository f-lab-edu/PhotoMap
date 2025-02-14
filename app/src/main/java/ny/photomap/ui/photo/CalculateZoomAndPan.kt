package ny.photomap.ui.photo

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize


data class ScaleAndOffsets(val scale: Float, val offsetX: Float, val offsetY: Float)

fun calculateScaleAndOffsets(
    scale: Float,
    zoom: Float,
    pan: Offset,
    minScale: Float,
    maxScale: Float,
    size: IntSize,
    offsetX: Float,
    offsetY: Float,
    slowMovement: Float,
): ScaleAndOffsets {
    val newScale = scale * zoom
    val resultScale = newScale.coerceIn(minScale, maxScale)
    // Calculate new offsets based on zoom and pan
    val centerX = size.width / 2
    val centerY = size.height / 2
    val offsetXChange = (centerX - offsetX) * (newScale / scale - 1)
    val offsetYChange = (centerY - offsetY) * (newScale / scale - 1)
    // Calculate min and max offsets
    val maxOffsetX = (size.width / 2) * (scale - 1)
    val minOffsetX = -maxOffsetX
    val maxOffsetY = (size.height / 2) * (scale - 1)
    val minOffsetY = -maxOffsetY
    // Update offsets while ensuring they stay within bounds
    var resultOffsetX = offsetX
    var resultOffsetY = offsetY
    if (scale * zoom <= maxScale) {
        resultOffsetX = (offsetX + pan.x * scale * slowMovement + offsetXChange)
            .coerceIn(minOffsetX, maxOffsetX)
        resultOffsetY = (offsetY + pan.y * scale * slowMovement + offsetYChange)
            .coerceIn(minOffsetY, maxOffsetY)
    }
    return ScaleAndOffsets(scale = resultScale, offsetX = resultOffsetX, offsetY = resultOffsetY)
}