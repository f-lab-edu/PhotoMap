package ny.photomap.data.datasource

import ny.photomap.data.db.PhotoLocationEntity
import ny.photomap.data.model.PhotoLocationData

interface PhotoDataSource {

    suspend fun fetchAllPhotoLocation(): List<PhotoLocationData>

    suspend fun fetchPhotoLocationAddedAfter(fetchTime: Long): List<PhotoLocationData>

    suspend fun saveLatestUpdateTime()

    suspend fun getLatestUpdateTime(): Long

    suspend fun saveAllPhotoLocation(list: List<PhotoLocationEntity>)

    suspend fun deleteAllPhotoLocation()

    suspend fun getPhotoLocation(
        latitude: Double,
        longitude: Double,
        range: Double,
    ): List<PhotoLocationEntity>

    suspend fun getPhotoLocation(
        latitude: Double,
        longitude: Double,
        range: Double,
        startTime: Long,
        endTime: Long,
    ): List<PhotoLocationEntity>

    suspend fun getPhotoLocation(
        northLatitude: Double,
        southLatitude: Double,
        eastLongitude: Double,
        westLongitude: Double,
    ): List<PhotoLocationEntity>

    suspend fun getPhotoLocation(
        northLatitude: Double,
        southLatitude: Double,
        eastLongitude: Double,
        westLongitude: Double,
        startTime: Long,
        endTime: Long,
    ): List<PhotoLocationEntity>


    suspend fun initializePhotoLocation(list: List<PhotoLocationEntity>)

    suspend fun getLatestPhotoLocation() : PhotoLocationEntity?

    // todo 추후 기능 추가
    /*    suspend fun getPhotoLocationWithOffset(
            latitude: Double,
            longitude: Double,
            range: Double,
            offset: Int,
            limit: Int,
        ):List<PhotoLocationEntity>

        suspend fun getPhotoLocationWithOffset(
            latitude: Double,
            longitude: Double,
            range: Double,
            startTime: Long,
            endTime: Long,
            offset: Int,
            limit: Int,
        ): List<PhotoLocationEntity>*/


}