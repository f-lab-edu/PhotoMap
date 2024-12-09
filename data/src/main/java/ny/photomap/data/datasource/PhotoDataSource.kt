package ny.photomap.data.datasource

import ny.photomap.data.db.PhotoLocationEntity
import ny.photomap.data.model.PhotoLocationData

interface PhotoDataSource {

    suspend fun fetchAllPhotoLocation(): Result<List<PhotoLocationData>>

    suspend fun fetchPhotoLocationAddedAfter(fetchTime: Long): Result<List<PhotoLocationData>>

    suspend fun saveLatestFetchTime(fetchTime: Long): Result<Unit>

    suspend fun getLatestFetchTime(): Result<Long>

    suspend fun saveAllPhotoLocation(list: List<PhotoLocationEntity>) : Result<Unit>

    suspend fun deleteAllPhotoLocation() : Result<Unit>

    suspend fun getPhotoLocation(
        latitude: Double,
        longitude: Double,
        range: Double,
    ): Result<List<PhotoLocationEntity>>

    suspend fun getPhotoLocation(
        latitude: Double,
        longitude: Double,
        range: Double,
        startTime: Long,
        endTime: Long,
    ): Result<List<PhotoLocationEntity>>

    // todo 추후 기능 추가
/*    suspend fun getPhotoLocationWithOffset(
        latitude: Double,
        longitude: Double,
        range: Double,
        offset: Int,
        limit: Int,
    ): Result<List<PhotoLocationEntity>>

    suspend fun getPhotoLocationWithOffset(
        latitude: Double,
        longitude: Double,
        range: Double,
        startTime: Long,
        endTime: Long,
        offset: Int,
        limit: Int,
    ): Result<List<PhotoLocationEntity>>*/


}