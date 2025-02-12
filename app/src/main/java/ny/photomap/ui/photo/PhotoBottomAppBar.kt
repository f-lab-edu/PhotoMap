package ny.photomap.ui.photo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ny.photomap.ui.theme.Typography


@Composable
fun PhotoBottomAppBar(
    location: String,
    dateTime: String,
    shownText: Boolean,
    textBackgroundColor: Color,
) {
    BottomAppBar(
        modifier = Modifier.wrapContentHeight(),
        contentColor = Color.White,
        containerColor = Color.Transparent
    ) {
        Spacer(
            modifier = Modifier
                .weight(1f)
                .padding(start = 10.dp)
        )
        if (shownText)
            Column(
                modifier = Modifier.padding(start = 2.dp, end = 2.dp, top = 2.dp, bottom = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (dateTime.isNotEmpty()) {
                    Text(
                        modifier = Modifier
                            .background(color = textBackgroundColor, shape = CircleShape)
                            .padding(top = 3.dp, bottom = 3.dp, start = 10.dp, end = 10.dp),
                        text = dateTime,
                        color = Color.White, style = Typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(Modifier.height(2.dp))
                if (location.isNotEmpty()) {
                    Text(
                        modifier = Modifier
                            .background(color = textBackgroundColor, shape = CircleShape)
                            .padding(top = 3.dp, bottom = 3.dp, start = 10.dp, end = 10.dp),
                        text = location,
                        color = Color.White, style = Typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }

        Spacer(
            modifier = Modifier
                .weight(1f)
                .padding(start = 10.dp)
        )
    }
}