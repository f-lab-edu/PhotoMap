package ny.photomap.ui.marker

import android.graphics.BitmapFactory
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ny.photomap.model.PhotoLocationUIModel

@Composable
fun PhotoLocationMarker(
    model: PhotoLocationUIModel,
    color: Color,
    text: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier,
        shape = RectangleShape,
        color = color,
        contentColor = Color.White,
        border = BorderStroke(1.dp, Color.White)
    ) {
        Box(contentAlignment = Alignment.Center) {
            val bitmap = BitmapFactory.decodeByteArray(model.thumbnail, 0, model.thumbnail.size)

            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = model.name,
                contentScale = ContentScale.Crop
            )
            Text(
                text,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )
        }
    }
}