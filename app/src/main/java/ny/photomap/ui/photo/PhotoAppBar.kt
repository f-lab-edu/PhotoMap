package ny.photomap.ui.photo

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import ny.photomap.ui.theme.PhotoMapTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoAppBar(location: String, shownText: Boolean) {
    val context = LocalContext.current
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            navigationIconContentColor = Color.White,
            titleContentColor = Color.White,
            actionIconContentColor = Color.White
        ),
        title = {
            if (shownText)
                Text(
                    text = location, color = Color.White
                )
        },
        navigationIcon = {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = context.getString(ny.photomap.R.string.go_back)
            )
        },
    )
}

@Preview
@Composable
fun PhotoAppBarPreview() {
    PhotoMapTheme {
        PhotoAppBar(
            location = "경기도 수원시 장안구 영화동 320-2",
            shownText = true
        )
    }
}