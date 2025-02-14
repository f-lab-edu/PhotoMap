package ny.photomap.ui.photo

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.palette.graphics.Palette
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.request.crossfade
import coil3.toBitmap
import kotlinx.coroutines.flow.collectLatest
import ny.photomap.ui.mainmap.SuccessFailureSnackbar

/**
 * reference : https://medium.com/globant/implementing-pinch-to-zoom-in-jetpack-compose-dc824155e313
 * todo : 배경도 같이 Zoom 되는 이슈. 수정 필요
 */
@Composable
fun PhotoScreen(
    viewModel: PhotoViewModel = hiltViewModel(),
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()
    var textBackgroundColor by remember { mutableStateOf(Color.Black) }
    var iconBackgroundColor by remember { mutableStateOf(Color.Black) }

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

    val configuration = LocalConfiguration.current
    val width = configuration.screenWidthDp.dp.value
    val height = configuration.screenHeightDp.dp.value

    val imageRequest = ImageRequest.Builder(context)
        .data(state.uri)
        .crossfade(true)
        .allowHardware(false)
        .listener(onSuccess = { _, successResult ->
            val palette = Palette.Builder(successResult.image.toBitmap()).generate()
            palette.mutedSwatch?.rgb?.let {
                textBackgroundColor = Color(it)
                iconBackgroundColor = Color(it)
            }
        })
        .build()

    Box {
        AsyncImage(
            modifier = Modifier
                .size((width * 2.5).dp, (height * 2.5).dp)
                .blur(
                    radiusX = 25.dp,
                    radiusY = 25.dp,
                    edgeTreatment = BlurredEdgeTreatment(RoundedCornerShape(8.dp))
                ),
            model = imageRequest,
            contentDescription = "background",
            contentScale = ContentScale.Crop
        )

        var shownLocationAndDate by remember { mutableStateOf(true) }

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                PhotoTopAppBar(
                    iconBackgroundColor = iconBackgroundColor,
                ) {
                    viewModel.goBack()
                }
            }, bottomBar = {
                PhotoBottomAppBar(
                    location = state.location ?: "",
                    dateTime = state.dateTime ?: "",
                    shownText = shownLocationAndDate,
                    textBackgroundColor = textBackgroundColor
                )
            }, snackbarHost = {
                SnackbarHost(snackBarHostState) {
                    SuccessFailureSnackbar(it)
                }

            }) { padding ->

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

            AsyncImage(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, _ ->

                            val (newScale, newOffsetX, newOffsetY) = calculateScaleAndOffsets(
                                scale = scale,
                                zoom = zoom,
                                pan = pan,
                                minScale = minScale,
                                maxScale = maxScale,
                                size = size,
                                offsetX = offsetX,
                                offsetY = offsetY,
                                slowMovement = slowMovement
                            )
                            scale = newScale
                            offsetX = newOffsetX
                            offsetY = newOffsetY
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
                            },
                            onTap = {
                                shownLocationAndDate = !shownLocationAndDate
                            }
                        )
                    }
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        translationX = offsetX
                        translationY = offsetY
                    },
                model = imageRequest,
                contentDescription = state.location,
                contentScale = ContentScale.Fit
            )
        }
    }
}