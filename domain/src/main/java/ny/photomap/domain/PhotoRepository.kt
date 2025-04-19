package ny.photomap.domain

import ny.photomap.domain.model.PhotoLocationRequestModel
import ny.photomap.domain.model.PhotoLocationEntityModel

interface PhotoRepository {

    suspend fun fetchAllPhotoLocation(): Result<List<PhotoLocationRequestModel>>

    suspend fun fetchPhotoLocationAddedAfter(fetchTime: Long): Result<List<PhotoLocationRequestModel>>

    suspend fun saveLatestUpdateTime(): Result<Unit>

    suspend fun getLatestUpdateTime(): Result<Long>

    suspend fun isSyncExpired(days: Int) : Result<Boolean>

    suspend fun saveAllPhotoLocation(list: List<PhotoLocationRequestModel>) : Result<Unit>

    suspend fun deleteAllPhotoLocation() : Result<Unit>

    suspend fun getPhotoLocation(id: Long) : Result<PhotoLocationEntityModel>

    suspend fun getPhotoLocation(
        latitude: Double,
        longitude: Double,
        range: Double,
        startTime: Long? = null,
        endTime: Long? = null,
    ): Result<List<PhotoLocationEntityModel>>

    suspend fun getPhotoLocation(
        northLatitude: Double,
        southLatitude: Double,
        eastLongitude: Double,
        westLongitude: Double,
        startTime: Long? = null,
        endTime: Long? = null,
    ): Result<List<PhotoLocationEntityModel>>

    suspend fun initializePhotoLocation(list: List<PhotoLocationRequestModel>) : Result<Unit>

    suspend fun getLatestPhotoLocation() : Result<PhotoLocationEntityModel?>

    suspend fun getLocationText(latitude: Double, longitude: Double) : String

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