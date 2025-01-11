package ny.photomap.model

data class AcceptPermissionState(
    val filePermission: FileAcceptPermissionState = FileAcceptPermissionState(),
    val locationPermission: LocationPermissionState = LocationPermissionState(),
)

data class FileAcceptPermissionState(
    val acceptedPermission: Boolean = false,
    val visualUserSelectedPermission: Boolean = false,
)

data class LocationPermissionState(
    val acceptedPermission: Boolean = false,
)
