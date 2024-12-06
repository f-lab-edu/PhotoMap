package ny.photomap.model

import android.net.Uri

data class PhotoLocationData(
    val uri: Uri,
    val name: String?,
    val latitude: Double,
    val longitude: Double,
    val generationTime: String?,
    val addTime: String?,
)
