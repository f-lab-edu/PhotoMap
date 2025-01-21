package ny.photomap.model

import android.net.Uri
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import ny.photomap.domain.model.PhotoLocationEntityModel

data class PhotoLocationUIModel(
    val id: Long,
    val name: String?,
    val location: LocationUIModel,
    val time: TimeUIModel,
    val uri: Uri,
) : ClusterItem {
    override fun getPosition(): LatLng = LatLng(location.latitude, location.longitude)

    override fun getTitle(): String? = name

    override fun getSnippet(): String? =
        time.takenTimeFormatString() ?: time.addedTimeFormatString()

    override fun getZIndex(): Float? = 0f
}

fun PhotoLocationEntityModel.toPhotoLocationUiModel(): PhotoLocationUIModel = PhotoLocationUIModel(
    id = id,
    name = name,
    location = LocationUIModel(latitude = latitude, longitude = longitude),
    time = TimeUIModel(takenTime = this.generatedTimeMillis, addedTime = addedTimeMillis),
    uri = Uri.parse(this.uri)
)