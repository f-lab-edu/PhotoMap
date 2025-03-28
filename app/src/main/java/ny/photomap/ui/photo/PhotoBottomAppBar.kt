package ny.photomap.ui.photo

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.BottomAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


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
                    CircleShapeBackgroundText(
                        backgroundColor = textBackgroundColor,
                        text = dateTime,
                        textColor = Color.White
                    )
                }
                Spacer(Modifier.height(2.dp))
                if (location.isNotEmpty()) {
                    CircleShapeBackgroundText(
                        backgroundColor = textBackgroundColor,
                        text = location,
                        textColor = Color.White
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