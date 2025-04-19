package ny.photomap.permission

import android.os.Build

val readImagePermissions: List<String>
    get() {
        val list = mutableListOf<String>(android.Manifest.permission.ACCESS_MEDIA_LOCATION)
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