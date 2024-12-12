package ny.photomap.model

import android.net.Uri
import ny.photomap.domain.model.PhotoLocationModel

data class PhotoLocationUiModel(
    val location: LocationModel,
    val time: TimeModel,
    val thumbnail: ByteArray?,
    val uri: Uri,
)

fun PhotoLocationModel.toUiModel(): PhotoLocationUiModel = PhotoLocationUiModel(
    location = LocationModel(latitude = latitude, longitude = longitude),
    time = TimeModel(takenTime = this.generatedTimeMillis, addedTime = addedTimeMillis),
    thumbnail = this.thumbNail,
    uri = Uri.parse(this.uri)
)