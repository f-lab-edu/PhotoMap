package ny.photomap

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import ny.photomap.permission.Permission
import ny.photomap.permission.PermissionStateCheckerImpl
import ny.photomap.permission.PermissionVisualUserSelected
import ny.photomap.permission.PermissionVisualUserSelectedStateCheckerImpl


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionRequester(
    permission: Permission,
    permissionsState: MultiplePermissionsState = rememberMultiplePermissionsState(permission.permission),
    onAllGranted: () -> Unit,
    onVisualUserSelectedGranted: (() -> Unit)? = null,
    onDenied: (shouldShowRationale: Boolean) -> Unit,
) {
    if (permission.permission.isEmpty()) return
    var permissionRequested: Boolean by remember { mutableStateOf(false) }

    if (permission is PermissionVisualUserSelected) {
        val permissionChecker = PermissionVisualUserSelectedStateCheckerImpl(permission)
        when {
            permissionChecker.isGranted(permissionsState.permissions) -> onAllGranted()
            else -> when {
                permissionChecker.isVisualUserSelectedGranted(permissionsState.permissions)
                    -> onVisualUserSelectedGranted?.invoke()

                permissionChecker.isDenied(permissionsState.permissions)
                    -> if (permissionRequested) {
                    onDenied(permissionChecker.shouldShowRationale(permissionsState.permissions))
                } else {
                    LaunchedEffect(Unit) {
                        permissionsState.launchMultiplePermissionRequest()
                        permissionRequested = !permissionRequested
                    }
                }

            }
        }
    } else {
        val permissionChecker = PermissionStateCheckerImpl(permission)
        when {
            permissionChecker.isGranted(permissionsState.permissions)
                -> onAllGranted()

            permissionChecker.isDenied(permissionsState.permissions)
                -> if (!permissionRequested) {
                LaunchedEffect(Unit) {
                    permissionsState.launchMultiplePermissionRequest()
                }
                permissionRequested = !permissionRequested
            } else {
                onDenied(permissionChecker.shouldShowRationale(permissionsState.permissions))
            }

        }
    }
}