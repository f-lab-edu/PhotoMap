package ny.photomap.ui.mainmap

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.PermissionStatus
import ny.photomap.R

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SyncWithPermissionNoticeExtendedFloatButton(
    modifier: Modifier = Modifier,
    permissionState: MultiplePermissionsState,
    targetPermissionList: List<String>,
    onClick: (hasPermission: Boolean) -> Unit,
) {
    var isExtended: Boolean by remember { mutableStateOf(false) }

    val targetPermission = targetPermissionList.firstOrNull()
    val visualUserSelectedPermission =
        if (targetPermissionList.size > 1) targetPermissionList.last() else null

    val isGranted = if (targetPermission == null) true else
        permissionState.permissions.find { it.permission == targetPermission }?.status == PermissionStatus.Granted

    val isGrantedVisualUserSelectedPermission = if (visualUserSelectedPermission == null) true else
        permissionState.permissions.find { it.permission == visualUserSelectedPermission }?.status == PermissionStatus.Granted

    val (containerColor, contentColor) = when {
        isGranted -> {
            MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.primary
        }

        isGrantedVisualUserSelectedPermission -> {
            MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.tertiary
        }

        else -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.error
    }


    Row(modifier = modifier) {
        if (isExtended) {

            val text = when {
                isGranted -> stringResource(R.string.button_sync)

                isGrantedVisualUserSelectedPermission -> stringResource(R.string.button_request_file_permission_with_user_visual_selected)

                else -> stringResource(R.string.button_request_file_permission_with_denied)
            }

            FilledTonalButton(
                modifier = Modifier
                    .wrapContentWidth(align = Alignment.End)
                    .padding(end = dimensionResource(R.dimen.margin_small)),
                colors = ButtonDefaults.filledTonalButtonColors().copy(
                    contentColor = contentColor,
                    containerColor = containerColor
                ), onClick = {
                    when {
                        isGranted -> onClick(true)
                        else -> onClick(false)
                    }
                    isExtended = !isExtended
                }) {
                Text(
                    text
                )
            }
        }

        SmallFloatingActionButton(
            modifier = Modifier.weight(weight = 1f, fill = false),
            onClick = {
                isExtended = !isExtended
            },
            containerColor = containerColor,
            contentColor = contentColor
        ) {
            BadgedBox(badge = {
                if (!isGranted && isGrantedVisualUserSelectedPermission) {
                    Badge(
                        containerColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondary
                    ) {
                        Text(stringResource(R.string.badge_alert))
                    }
                } else if (!isGranted) {
                    Badge(
                        containerColor = MaterialTheme.colorScheme.onErrorContainer,
                        contentColor = MaterialTheme.colorScheme.onError
                    ) {
                        Text(stringResource(R.string.badge_no))
                    }
                }
            }) {
                Icon(Icons.Filled.Refresh, "Small floating action button.")
            }

        }
    }
}