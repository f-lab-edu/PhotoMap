package ny.photomap.permission

import android.Manifest
import android.os.Build
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.PermissionStatus

@OptIn(ExperimentalPermissionsApi::class)
fun MultiplePermissionsState.isGranted(permission: String?): Boolean =
    if (permission == null) true else
        this.permissions.any { it.permission == permission && it.status == PermissionStatus.Granted }


@OptIn(ExperimentalPermissionsApi::class)
fun MultiplePermissionsState.isAllGranted(permissions: List<String>): Boolean =
    this.permissions.filter { permissions.contains(it.permission) }
        .all { it.status == PermissionStatus.Granted }

@OptIn(ExperimentalPermissionsApi::class)
fun MultiplePermissionsState.isDenied(permission: String?): Boolean =
    if (permission == null) false else
        this.permissions.any { it.permission == permission && it.status is PermissionStatus.Denied }

@OptIn(ExperimentalPermissionsApi::class)
fun MultiplePermissionsState.isGrantedReadMediaVisualUserSelected(): Boolean =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        this.permissions.find { it.permission == Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED }?.status ==
                PermissionStatus.Granted
    } else false