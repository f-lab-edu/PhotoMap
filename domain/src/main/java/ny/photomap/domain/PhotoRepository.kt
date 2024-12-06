package ny.photomap.domain

import ny.photomap.domain.model.PhotoLocationModel

interface PhotoRepository {

    suspend fun fetchAllPhotoLocation(): Result<Boolean>

    suspend fun fetchPhotoLocationAddedAfter(fetchTime: Long): Result<Boolean>

    suspend fun saveLatestFetchTime(fetchTime: Long): Result<Boolean>

    suspend fun getLatestFetchTime(): Result<Long>

    suspend fun getPhotoLocation(
        latitude: Double,
        longitude: Double,
        range: Double,
    ): Result<List<PhotoLocationModel>>

    suspend fun getPhotoLocationWithOffset(
        latitude: Double,
        longitude: Double,
        range: Double,
        offset: Int,
        limit: Int,
    ): Result<List<PhotoLocationModel>>

    suspend fun getPhotoLocation(
        latitude: Double,
        longitude: Double,
        range: Double,
        startTime: Long,
        endTime: Long,
    ): Result<List<PhotoLocationModel>>

    suspend fun getPhotoLocationWithOffset(
        latitude: Double,
        longitude: Double,
        range: Double,
        startTime: Long,
        endTime: Long,
        offset: Int,
        limit: Int,
    ): Result<List<PhotoLocationModel>>
}