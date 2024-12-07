package ny.photomap.data.datasource

import android.content.ContentResolver
import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import androidx.core.database.getStringOrNull
import androidx.core.os.bundleOf
import androidx.exifinterface.media.ExifInterface
import androidx.exifinterface.media.ExifInterface.TAG_DATETIME
import androidx.exifinterface.media.ExifInterface.TAG_DATETIME_ORIGINAL
import ny.photomap.data.model.PhotoLocationData
import javax.inject.Inject

class PhotoDataSourceImpl @Inject constructor(private val contentResolver: ContentResolver) :
    PhotoDataSource {

    private val projection = arrayOf(
        MediaStore.Images.Media._ID,
        MediaStore.Images.Media.DISPLAY_NAME
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
    fun queryToList(query: Cursor?): Result<List<PhotoLocationData>> {
        val list = mutableListOf<PhotoLocationData>()
        return try {
            query?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val uri =
                        ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                    val name = cursor.getStringOrNull(nameColumn)

                    getExifInterface(uri)?.let {
                        it.latLong?.let { (latitude, longitude) ->
                            PhotoLocationData(
                                uri = uri,
                                name = name,
                                latitude = latitude,
                                longitude = longitude,
                                generationTime = it.getAttribute(TAG_DATETIME_ORIGINAL),
                                addTime = it.getAttribute(TAG_DATETIME),
                                thumbNail = it.thumbnail
                            )

                        }
                    }?.let { data ->
                        list.add(data)
                    }
                }
            }
            Result.success(list)
        } catch (e: Exception) {
            Result.failure<List<PhotoLocationData>>(e)
        }
    }

    override suspend fun fetchAllPhotoLocation(): Result<List<PhotoLocationData>> {
        return queryToList(getQuery())
    }

    override suspend fun fetchPhotoLocationAddedAfter(fetchTime: Long): Result<List<PhotoLocationData>> {
        return queryToList(getQueryAfter(fetchTime))
    }

    override suspend fun saveLatestFetchTime(fetchTime: Long): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun getLatestFetchTime(): Result<Long> {
        TODO("Not yet implemented")
    }

    override suspend fun getPhotoLocation(
        latitude: Double,
        longitude: Double,
        range: Double,
    ): Result<List<PhotoLocationData>> {
        TODO("Not yet implemented")
    }

    override suspend fun getPhotoLocation(
        latitude: Double,
        longitude: Double,
        range: Double,
        startTime: Long,
        endTime: Long,
    ): Result<List<PhotoLocationData>> {
        TODO("Not yet implemented")
    }

    override suspend fun getPhotoLocationWithOffset(
        latitude: Double,
        longitude: Double,
        range: Double,
        offset: Int,
        limit: Int,
    ): Result<List<PhotoLocationData>> {
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
    ): Result<List<PhotoLocationData>> {
        TODO("Not yet implemented")
    }

    fun getExifInterface(uri: Uri): ExifInterface? =
        contentResolver.openInputStream(uri)?.use { inputStream ->
            ExifInterface(inputStream)
        }
}