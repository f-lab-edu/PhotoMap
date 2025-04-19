package ny.photomap.data.datasource

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.location.Address
import android.location.Geocoder
import android.location.Geocoder.GeocodeListener
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.database.getStringOrNull
import androidx.core.os.bundleOf
import androidx.exifinterface.media.ExifInterface
import androidx.exifinterface.media.ExifInterface.TAG_DATETIME_ORIGINAL
import androidx.exifinterface.media.ExifInterface.TAG_OFFSET_TIME_ORIGINAL
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import ny.photomap.data.db.PhotoLocationDao
import ny.photomap.data.db.PhotoLocationEntity
import ny.photomap.data.model.PhotoLocationData
import ny.photomap.data.preferences.PhotoLocationReferences
import timber.log.Timber
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject
import kotlin.collections.first

class PhotoDataSourceImpl @Inject constructor(
    private val contentResolver: ContentResolver,
    private val photoLocationDao: PhotoLocationDao,
    private val preferences: PhotoLocationReferences,
    @ApplicationContext private val context: Context,
) : PhotoDataSource {

    private val projection = arrayOf(
        MediaStore.Images.Media._ID,
        MediaStore.Images.Media.DISPLAY_NAME,
        MediaStore.Images.Media.DATE_ADDED,
        MediaStore.Images.Media.DATE_TAKEN
    )

    private val selectionFromDate = "${MediaStore.Images.Media.DATE_ADDED} >= ?"

    // 최초 앱 실행 시 + 사용자가 싱크를 원할 경우
    fun getQuery(): Cursor? {
        return contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null
        )
    }

    // 최초 앱 호출이 아닐 경우, 앱 실행 시
    @Throws(RuntimeException::class)
    fun getQueryAfter(fromDate: Long): Cursor? {
        return contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            bundleOf(
                ContentResolver.QUERY_ARG_SQL_SELECTION to selectionFromDate,
                ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS to arrayOf(fromDate.toString())
            ),
            null
        )
    }


    fun queryToList(query: Cursor?): List<PhotoLocationData> {
        val list = mutableListOf<PhotoLocationData>()
        query?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val addedTimeColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
            val takenTimeColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val uri =
                    ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                val name = cursor.getStringOrNull(nameColumn)
                val addedTime: Long? = cursor.getLong(addedTimeColumn)
                val takenTime: Long? = cursor.getLong(takenTimeColumn)

                createPhotoLocationData(
                    uri = uri,
                    name = name,
                    addedTime = addedTime,
                    takenTime = takenTime,
                )?.let { data ->
                    list.add(data)
                }
            }
        }
        return list
    }

    fun createPhotoLocationData(
        uri: Uri,
        name: String?,
        addedTime: Long?,
        takenTime: Long?,
    ): PhotoLocationData? {
        return getExifInterface(uri)?.let {
            val generatedTime =
                getTimestampWithOffset(
                    it.getAttribute(TAG_DATETIME_ORIGINAL),
                    it.getAttribute(TAG_OFFSET_TIME_ORIGINAL)
                ) ?: takenTime

            val latLong = it.latLong
            if (generatedTime != null && addedTime != null && latLong != null && latLong.size == 2) {
                val latitude = latLong[0]
                val longitude = latLong[1]
                PhotoLocationData(
                    uri = uri,
                    name = name,
                    latitude = latitude,
                    longitude = longitude,
                    generatedTime = generatedTime,
                    addedTime = addedTime,
                )
            } else null
        }
    }


    fun getTimestampWithOffset(timeString: String?, offsetTimeString: String?): Long? {
        return if (timeString != null && offsetTimeString != null) {
            val formatter = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss")
            val photoTakenTime = LocalDateTime.parse(timeString, formatter)
            val zoneOffset = ZoneOffset.of(offsetTimeString)
            photoTakenTime.toInstant(zoneOffset).toEpochMilli()
        } else null
    }

    override suspend fun fetchAllPhotoLocation(): List<PhotoLocationData> =
        withContext(Dispatchers.IO) {
            queryToList(getQuery())
        }

    override suspend fun fetchPhotoLocationAddedAfter(fetchTime: Long): List<PhotoLocationData> =
        withContext(Dispatchers.IO) {
            queryToList(getQueryAfter(fetchTime))
        }

    override suspend fun saveLatestUpdateTime(lastSyncTime: Long) {
        return preferences.updateTimeSyncDatabase(lastSyncTime)
    }

    override suspend fun getLatestUpdateTime(): Long {
        return preferences.timeSyncDatabaseFlow.first()
    }

    override suspend fun saveAllPhotoLocation(list: List<PhotoLocationEntity>) {
        return photoLocationDao.insertAll(list)

    }

    override suspend fun deleteAllPhotoLocation() {
        return photoLocationDao.deleteAll()
    }

    override suspend fun getPhotoLocation(id: Long): PhotoLocationEntity {
        return photoLocationDao.getLocation(id = id)
    }

    override suspend fun getPhotoLocation(
        latitude: Double,
        longitude: Double,
        range: Double,
    ): List<PhotoLocationEntity> =
        photoLocationDao.getLocationOf(
            latitude = latitude,
            longitude = longitude,
            range = range
        )


    override suspend fun getPhotoLocation(
        latitude: Double,
        longitude: Double,
        range: Double,
        startTime: Long,
        endTime: Long,
    ): List<PhotoLocationEntity> =
        photoLocationDao.getLocationAndDateOf(
            latitude = latitude,
            longitude = longitude,
            range = range,
            fromTime = startTime,
            toTime = endTime
        )

    override suspend fun getPhotoLocation(
        northLatitude: Double,
        southLatitude: Double,
        eastLongitude: Double,
        westLongitude: Double,
    ): List<PhotoLocationEntity> {
        return photoLocationDao.getLocationOf(
            northLatitude = northLatitude,
            southLatitude = southLatitude,
            eastLongitude = eastLongitude,
            westLongitude = westLongitude
        )
    }

    override suspend fun getPhotoLocation(
        northLatitude: Double,
        southLatitude: Double,
        eastLongitude: Double,
        westLongitude: Double,
        startTime: Long,
        endTime: Long,
    ): List<PhotoLocationEntity> {
        return photoLocationDao.getLocationAndDateOf(
            northLatitude = northLatitude,
            southLatitude = southLatitude,
            eastLongitude = eastLongitude,
            westLongitude = westLongitude,
            fromTime = startTime,
            toTime = endTime
        )
    }

    fun getExifInterface(uri: Uri): ExifInterface? =
        contentResolver.openInputStream(uri)?.use { inputStream ->
            ExifInterface(inputStream)
        }

    override suspend fun initializePhotoLocation(list: List<PhotoLocationEntity>) {
        photoLocationDao.initialize(entityList = list)
    }

    override suspend fun getLatestPhotoLocation(): PhotoLocationEntity? {
        return photoLocationDao.getLatest()
    }

    override suspend fun getLocationText(latitude: Double, longitude: Double): String = withContext(
        Dispatchers.IO
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            suspendCancellableCoroutine<String> { continuation ->
                Geocoder(context, Locale.getDefault())
                    .getFromLocation(latitude, longitude, 1, object : GeocodeListener {
                        override fun onGeocode(addresses: List<Address?>) {
                            val address = addresses.first()?.getAddressLine(0) ?: ""
                            continuation.resume(address) { cause, _, _ ->
                                Timber.e(cause)
                            }
                        }

                        override fun onError(errorMessage: String?) {
                            super.onError(errorMessage)
                            continuation.resume("") { cause, _, _ ->
                                Timber.e(cause)
                            }
                        }
                    })
                continuation.invokeOnCancellation {
                    Timber.e(it)
                }
            }
        } else {
            val addressList = Geocoder(context, Locale.getDefault())
                .getFromLocation(latitude, longitude, 1)
            addressList?.first()?.getAddressLine(0) ?: ""
        }
    }

    // todo 추후 기능 추가
    /* override suspend fun getPhotoLocationWithOffset(
         latitude: Double,
         longitude: Double,
         range: Double,
         offset: Int,
         limit: Int,
     ): List<PhotoLocationEntity> {
         TODO("Not yet implemented")
     }

     override suspend fun getPhotoLocationWithOffset(
         latitude: Double,
         longitude: Double,
         range: Double,
         startTime: Long,
         endTime: Long,
         offset: Int,
         limit: Int,
     ): List<PhotoLocationEntity> {
         TODO("Not yet implemented")
     }*/

}