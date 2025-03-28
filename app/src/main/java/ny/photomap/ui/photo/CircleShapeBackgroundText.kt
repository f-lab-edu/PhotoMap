package ny.photomap.ui.photo

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import ny.photomap.ui.theme.Typography

@Composable
fun CircleShapeBackgroundText(modifier: Modifier = Modifier, backgroundColor: Color, text: String, textColor : Color) {
    Text(
        modifier = modifier.circleShapeForText(backgroundColor),
        text = text,
        color = textColor, style = Typography.bodyMedium,
        textAlign = TextAlign.Center
    )
}