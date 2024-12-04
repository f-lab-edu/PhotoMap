package ny.photomap.data

import android.content.ContentResolver
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import ny.photomap.model.PhotoInfo

class PhotoRepository(val contentResolver: ContentResolver, val dataSource: PhotoDataSource) {

    fun getAllPhotoInfoList(): List<PhotoInfo> {
        return dataSource.getAllPhotoUriList().mapNotNull {
            convertPhotoInfo(it)
        }
    }

    fun getDateRangePhotoInfoList(startMilliSecond: Long, endMilliSecond: Long): List<PhotoInfo> {
        return dataSource.getDateRangePhotoUriList(
            startMilliSecond = startMilliSecond,
            endMilliSecond = endMilliSecond
        ).mapNotNull {
            convertPhotoInfo(it)
        }
    }

    fun getLocationPhotoInfoList(
        targetLatitude: Double,
        targetLongitude: Double,
        surroundRange: Double,
    ): List<PhotoInfo> {
        return dataSource.getAllPhotoUriList().mapNotNull {
            convertPhotoInfo(
                uri = it,
                targetLatitude = targetLatitude,
                targetLongitude = targetLongitude,
                surroundRange = surroundRange
            )
        }
    }

    fun convertPhotoInfo(uri: Uri): PhotoInfo? = getExifInterface(uri)?.let {
        it.latLong?.let { (latitude, longitude) ->
            PhotoInfo(
                uri = uri,
                latitude = latitude,
                longitude = longitude,
                generationTime = it.generationTime
            )
        }
    }

    fun convertPhotoInfo(
        uri : Uri,
        targetLatitude: Double,
        targetLongitude: Double,
        surroundRange: Double,
    ): PhotoInfo? =
        getExifInterface(uri)?.let {
            it.latLong?.let { (latitude, longitude) ->
                // todo 근처 위치 판단 로직
                if (targetLatitude in latitude - surroundRange..latitude + surroundRange
                    && targetLongitude in longitude - surroundRange..longitude + surroundRange
                ) {
                    PhotoInfo(
                        uri = uri,
                        latitude = latitude,
                        longitude = longitude,
                        generationTime = it.generationTime
                    )
                } else null
            }
        }


    fun getExifInterface(uri: Uri): ExifInterface? =
        contentResolver.openInputStream(uri)?.use { inputStream ->
            ExifInterface(inputStream)
        }

    val ExifInterface.generationTime: String?
        get() = getAttribute(ExifInterface.TAG_DATETIME_ORIGINAL)
            ?: getAttribute(ExifInterface.TAG_DATETIME)
            ?: getAttribute(ExifInterface.TAG_DATETIME_DIGITIZED)
}