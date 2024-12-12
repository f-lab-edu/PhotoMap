package ny.photomap.permission

import android.os.Build

data class MediaPermissionData(
    val permission: String?,
    val visualUserSelectedPermission: String? = null,
) {
    val permissionList: List<String>?
        get() = if (permission != null && visualUserSelectedPermission != null) {
            listOf(permission, visualUserSelectedPermission)
        } else if (permission != null) {
            listOf(permission)
        } else null
}

val readImagePermission = MediaPermissionData(
    permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) android.Manifest.permission.READ_MEDIA_IMAGES
    else null,
    visualUserSelectedPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
        android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED else null
)

val readImagePermissions: List<String>
    get() {
        val list = mutableListOf<String>()
        val permission : String? =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) android.Manifest.permission.READ_MEDIA_IMAGES
            else null

        if(permission != null) {
            list.add(permission)
            val visualUserSelectedPermission =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
                    android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED else null
            if(visualUserSelectedPermission != null) list.add(visualUserSelectedPermission)
        }

        return list
    }

val locationPermissions : List<String> = listOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION)







