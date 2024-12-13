package ny.photomap.model

import android.net.Uri
import ny.photomap.domain.model.PhotoLocationModel

data class PhotoLocationUIModel(
    val location: LocationUIModel,
    val time: TimeUIModel,
    val thumbnail: ByteArray?,
    val uri: Uri,
)

fun PhotoLocationModel.toPhotoLocationUiModel(): PhotoLocationUIModel = PhotoLocationUIModel(
    location = LocationUIModel(latitude = latitude, longitude = longitude),
    time = TimeUIModel(takenTime = this.generatedTimeMillis, addedTime = addedTimeMillis),
    thumbnail = this.thumbNail,
    uri = Uri.parse(this.uri)
)