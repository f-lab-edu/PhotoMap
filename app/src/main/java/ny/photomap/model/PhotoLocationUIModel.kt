package ny.photomap.model

import android.net.Uri
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import ny.photomap.domain.model.PhotoLocationModel

data class PhotoLocationUIModel(
    val name: String?,
    val location: LocationUIModel,
    val time: TimeUIModel,
//    val thumbnail: ByteArray,
    val uri: Uri,
) : ClusterItem {
    override fun getPosition(): LatLng = LatLng(location.latitude, location.longitude)

    override fun getTitle(): String? = name


    override fun getSnippet(): String? =
        time.takenTimeFormatString() ?: time.addedTimeFormatString()

    override fun getZIndex(): Float? = 0f
}

fun PhotoLocationModel.toPhotoLocationUiModel(): PhotoLocationUIModel = PhotoLocationUIModel(
    name = name,
    location = LocationUIModel(latitude = latitude, longitude = longitude),
    time = TimeUIModel(takenTime = this.generatedTimeMillis, addedTime = addedTimeMillis),
//    thumbnail = this.thumbNail,
    uri = Uri.parse(this.uri)
)