package ny.photomap.domain

import ny.photomap.domain.model.PhotoLocationModel

interface PhotoRepository {

    suspend fun fetchAllPhotoLocation(): Result<List<PhotoLocationModel>>

    suspend fun fetchPhotoLocationAddedAfter(fetchTime: Long): Result<List<PhotoLocationModel>>

    suspend fun saveLatestUpdateTime(): Result<Unit>

    suspend fun getLatestUpdateTime(): Result<Long>

    suspend fun isSyncExpired(days: Int) : Result<Boolean>

    suspend fun saveAllPhotoLocation(list: List<PhotoLocationModel>) : Result<Unit>

    suspend fun deleteAllPhotoLocation() : Result<Unit>

    suspend fun getPhotoLocation(
        latitude: Double,
        longitude: Double,
        range: Double,
        startTime: Long? = null,
        endTime: Long? = null,
    ): Result<List<PhotoLocationModel>>

    suspend fun getPhotoLocation(
        northLatitude: Double,
        southLatitude: Double,
        eastLongitude: Double,
        westLongitude: Double,
        startTime: Long? = null,
        endTime: Long? = null,
    ): Result<List<PhotoLocationModel>>

    suspend fun initializePhotoLocation(list: List<PhotoLocationModel>) : Result<Unit>

    suspend fun getLatestPhotoLocation() : Result<PhotoLocationModel?>

    // todo 추후 작업
    /*suspend fun getPhotoLocationWithOffset(
        latitude: Double,
        longitude: Double,
        range: Double,
        offset: Int,
        limit: Int,
    ): Result<List<PhotoLocationModel>>

    suspend fun getPhotoLocationWithOffset(
        latitude: Double,
        longitude: Double,
        range: Double,
        startTime: Long,
        endTime: Long,
        offset: Int,
        limit: Int,
    ): Result<List<PhotoLocationModel>>*/
}