package ny.photomap.model

import android.net.Uri

data class PhotoInfo(
    val uri: Uri,
    val latitude: Double,
    val longitude: Double,
    val generationTime: String?,
)
