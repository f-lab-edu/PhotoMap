package ny.photomap.data.model

import android.net.Uri
import ny.photomap.domain.model.PhotoLocationRequestModel

data class PhotoLocationData(
    val uri: Uri,
    val name: String?,
    val latitude: Double,
    val longitude: Double,
    val generatedTime: Long,
    val addedTime: Long,
) : ModelMapper<PhotoLocationRequestModel> {
    override fun toModel(): PhotoLocationRequestModel = PhotoLocationRequestModel(
        uri = this@PhotoLocationData.uri.toString(),
        name = name,
        latitude = latitude,
        longitude = longitude,
        generatedTimeMillis = generatedTime,
        addedTimeMillis = addedTime,
    )
}
