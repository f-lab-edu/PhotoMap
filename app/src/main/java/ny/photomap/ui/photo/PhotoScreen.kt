package ny.photomap.ui.photo

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.request.crossfade
import coil3.size.pxOrElse
import kotlinx.coroutines.flow.collectLatest
import ny.photomap.ui.mainmap.SuccessFailureSnackbar

/**
 * reference : https://medium.com/globant/implementing-pinch-to-zoom-in-jetpack-compose-dc824155e313
 * todo : 배경도 같이 Zoom 되는 이슈. 수정 필요
 */
@Composable
fun PhotoScreen(
    modifier: Modifier,
    viewModel: PhotoViewModel = hiltViewModel(),
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.handleIntent(PhotoIntent.LoadPhoto)
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is PhotoEffect.Error -> snackBarHostState.showSnackbar(
                    message = context.getString(
                        effect.message
                    )
                )
            }
        }
    }

    Scaffold(modifier, topBar = {
//        PhotoAppBar()
    }, snackbarHost = {
        SnackbarHost(snackBarHostState) {
            SuccessFailureSnackbar(it)
        }

    }) { padding ->
        val imageRequest = ImageRequest.Builder(context)
            .data(state.uri)
            .crossfade(true)
            .allowHardware(false)
            .build()

        var imageSize by remember { mutableStateOf(Size(0f, 0f)) }

        val configuration = LocalConfiguration.current
        val width = configuration.screenWidthDp.dp.value
        val height = configuration.screenHeightDp.dp.value

        LaunchedEffect(Unit) {
            imageSize = imageRequest.sizeResolver.size().let {
                Size(
                    it.width.pxOrElse { width.toInt() }.toFloat(),
                    it.height.pxOrElse { height.toInt() }.toFloat()
                )
            }
        }

        // Mutable state variables to hold scale and offset values
        var scale by remember { mutableFloatStateOf(1f) }
        var offsetX by remember { mutableFloatStateOf(0f) }
        var offsetY by remember { mutableFloatStateOf(0f) }

        val minScale = 1f
        val maxScale = 4f

        // Remember the initial offset
        var initialOffset by remember { mutableStateOf(Offset(0f, 0f)) }

        // Coefficient for slowing down movement
        val slowMovement = 0.5f

        Box(
            modifier = Modifier.padding(padding)
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        // Update scale with the zoom
                        val newScale = scale * zoom
                        scale = newScale.coerceIn(minScale, maxScale)

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
                        if (scale * zoom <= maxScale) {
                            offsetX = (offsetX + pan.x * scale * slowMovement + offsetXChange)
                                .coerceIn(minOffsetX, maxOffsetX)
                            offsetY = (offsetY + pan.y * scale * slowMovement + offsetYChange)
                                .coerceIn(minOffsetY, maxOffsetY)
                        }

                        // Store initial offset on pan
                        if (pan != Offset(0f, 0f) && initialOffset == Offset(0f, 0f)) {
                            initialOffset = Offset(offsetX, offsetY)
                        }
                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = {
                            // Reset scale and offset on double tap
                            if (scale != 1f) {
                                scale = 1f
                                offsetX = initialOffset.x
                                offsetY = initialOffset.y
                            } else {
                                scale = 2f
                            }
                        }
                    )
                }
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    translationX = offsetX
                    translationY = offsetY
                }

        ) {
            AsyncImage(
                modifier = Modifier.fillMaxSize(),
                model = imageRequest,
                contentDescription = state.location,
                contentScale = ContentScale.Fit
            )
        }
    }
}