package ny.photomap.data.model

import android.net.Uri
import ny.photomap.domain.model.PhotoLocationModel

data class PhotoLocationData(
    val uri: Uri,
    val name: String?,
    val latitude: Double,
    val longitude: Double,
    val generatedTime: Long,
    val addedTime: Long,
    val thumbNail: ByteArray?,
) : ModelMapper<PhotoLocationModel> {
    override fun toModel(): PhotoLocationModel = PhotoLocationModel(
        uriString = uri.toString(),
        name = name,
        latitude = latitude,
        longitude = longitude,
        generatedTimeMillis = generatedTime,
        addedTimeMillis = addedTime,
        thumbNail = thumbNail
    )
}
