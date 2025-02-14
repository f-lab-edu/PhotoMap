package ny.photomap.ui.photo

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ny.photomap.ui.theme.PhotoMapTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoTopAppBar(
    iconBackgroundColor: Color = Color.Black,
    onClickIcon: () -> Unit,
) {
    val context = LocalContext.current
    TopAppBar(
        modifier = Modifier.wrapContentHeight(),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            navigationIconContentColor = Color.White,
            titleContentColor = Color.White,
            actionIconContentColor = Color.White
        ),
        title = {},
        navigationIcon = {
            Icon(
                modifier = Modifier
                    .padding(start = 5.dp)
                    .background(color = iconBackgroundColor, shape = CircleShape)
                    .padding(10.dp)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { onClickIcon() },
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
        PhotoTopAppBar {}
    }
}