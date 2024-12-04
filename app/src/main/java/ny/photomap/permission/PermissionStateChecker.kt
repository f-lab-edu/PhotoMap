package ny.photomap.permission

import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.shouldShowRationale


@OptIn(ExperimentalPermissionsApi::class)
interface PermissionStateChecker {
    fun isGranted(permissionState: List<PermissionState>): Boolean
    fun isDenied(permissionState: List<PermissionState>): Boolean
    fun shouldShowRationale(permissionState: List<PermissionState>): Boolean
}

@OptIn(ExperimentalPermissionsApi::class)
interface PermissionVisualUserSelectedStateChecker : PermissionStateChecker {
    fun isVisualUserSelectedGranted(permissionState: List<PermissionState>): Boolean
}

@OptIn(ExperimentalPermissionsApi::class)
class PermissionVisualUserSelectedStateCheckerImpl(val permission: PermissionVisualUserSelected) :
    PermissionVisualUserSelectedStateChecker {
    override fun isVisualUserSelectedGranted(permissionState: List<PermissionState>): Boolean =
        permissionState.any { it.permission == permission.mainPermission && it.status.isGranted }

    override fun isGranted(permissionState: List<PermissionState>): Boolean =
        permissionState.any { it.permission == permission.visualUserSelectedPermission && it.status.isGranted }

    override fun isDenied(permissionState: List<PermissionState>): Boolean =
        permissionState.all { !it.status.isGranted }

    override fun shouldShowRationale(permissionState: List<PermissionState>): Boolean =
        permissionState.any { it.permission == permission.mainPermission && it.status.shouldShowRationale }

}

@OptIn(ExperimentalPermissionsApi::class)
class PermissionStateCheckerImpl(val permission: Permission) : PermissionStateChecker {
    override fun isGranted(permissionState: List<PermissionState>): Boolean =
        permissionState.any { it.permission == permission.permission[0] && it.status.isGranted }

    override fun isDenied(permissionState: List<PermissionState>): Boolean =
        permissionState.all { !it.status.isGranted }

    override fun shouldShowRationale(permissionState: List<PermissionState>): Boolean =
        permissionState.any { it.permission == permission.permission[0] && it.status.shouldShowRationale }

}