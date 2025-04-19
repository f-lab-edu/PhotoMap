package ny.photomap.ui.photo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import ny.photomap.R

@Composable
fun Modifier.circleShapeForText(backgroundColor: Color) =
    this
        .background(color = backgroundColor, shape = CircleShape)
        .padding(
            top = dimensionResource(R.dimen.padding_circle_shape_top),
            bottom = dimensionResource(R.dimen.padding_circle_shape_top),
            start = dimensionResource(R.dimen.padding_circle_shape_start),
            end = dimensionResource(R.dimen.padding_circle_shape_end)
        )