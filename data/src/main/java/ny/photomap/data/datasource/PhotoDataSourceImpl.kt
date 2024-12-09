package ny.photomap.data.datasource

import android.content.ContentResolver
import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import androidx.core.database.getStringOrNull
import androidx.core.os.bundleOf
import androidx.exifinterface.media.ExifInterface
import androidx.exifinterface.media.ExifInterface.TAG_DATETIME_ORIGINAL
import androidx.exifinterface.media.ExifInterface.TAG_OFFSET_TIME_ORIGINAL
import kotlinx.coroutines.flow.first
import ny.photomap.data.db.PhotoLocationDao
import ny.photomap.data.db.PhotoLocationEntity
import ny.photomap.data.model.PhotoLocationData
import ny.photomap.data.preferences.PhotoLocationPreferencesImpl
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class PhotoDataSourceImpl @Inject constructor(
    private val contentResolver: ContentResolver,
    private val photoLocationDao: PhotoLocationDao,
    private val preferences: PhotoLocationPreferencesImpl,
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


    // todo : 멘토님께 질문 - 에러 발생 시 list에 담겨져 있는 유효한 정보도 전달하지 못하게 됨. 이런 처리의 아쉬움.
    fun queryToList(query: Cursor?): List<PhotoLocationData> {
        val list = mutableListOf<PhotoLocationData>()
        return try {
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
                    var takenTime: Long? = cursor.getLong(takenTimeColumn)

                    getExifInterface(uri)?.let {
                        val generatedTime =
                            getTimestampWithOffset(
                                it.getAttribute(TAG_DATETIME_ORIGINAL),
                                it.getAttribute(TAG_OFFSET_TIME_ORIGINAL)
                            ) ?: takenTime
                        if (generatedTime != null && addedTime != null && it.latLong != null) {
                            val latitude = it.latLong!![0]
                            val longitude = it.latLong!![1]
                            PhotoLocationData(
                                uri = uri,
                                name = name,
                                latitude = latitude,
                                longitude = longitude,
                                generatedTime = generatedTime,
                                addedTime = addedTime,
                                thumbNail = it.thumbnail
                            )
                        } else null
                    }?.let { data ->
                        list.add(data)
                    }
                }
            }
            list
        } catch (e: Exception) {
            throw e
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

    override suspend fun fetchAllPhotoLocation(): List<PhotoLocationData> {
        return queryToList(getQuery())
    }

    override suspend fun fetchPhotoLocationAddedAfter(fetchTime: Long): List<PhotoLocationData> {
        return queryToList(getQueryAfter(fetchTime))
    }

    override suspend fun saveLatestFetchTime(fetchTime: Long) {
        return preferences.updateTimeSyncDatabase(fetchTime)
    }

    override suspend fun getLatestFetchTime(): Long {
        return preferences.timeSyncDatabaseFlow.first()
    }

    override suspend fun saveAllPhotoLocation(list: List<PhotoLocationEntity>) {
        return photoLocationDao.insertAll(list)

    }

    override suspend fun deleteAllPhotoLocation() {
        return photoLocationDao.deleteAll()

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


    fun getExifInterface(uri: Uri): ExifInterface? =
        contentResolver.openInputStream(uri)?.use { inputStream ->
            ExifInterface(inputStream)
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