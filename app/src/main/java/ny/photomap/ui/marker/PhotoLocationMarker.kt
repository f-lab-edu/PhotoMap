package ny.photomap.ui.marker

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.request.crossfade
import ny.photomap.model.PhotoLocationUIModel


@Composable
fun PhotoLocationMarker(
    model: PhotoLocationUIModel,
    text: String,
    modifier: Modifier = Modifier,
) {

    val context = LocalContext.current

    Box(
        modifier
            .background(shape = RectangleShape, color = Color.Transparent)
    ) {

        AsyncImage(
            modifier = Modifier
                .fillMaxSize()
                .border(BorderStroke(1.dp, Color.White)),
            model = ImageRequest.Builder(context)
                .data(model.uri)
                .crossfade(true)
                .allowHardware(false)
                .build(),
            contentScale = ContentScale.Crop,
            contentDescription = null
        )

        Text(
            text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center,
            color = Color.White,
            modifier = Modifier.align(Alignment.Center)
        )
    }

}