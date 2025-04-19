package ny.photomap.data.repository

import ny.photomap.data.TimeStamp
import ny.photomap.data.datasource.PhotoDataSource
import ny.photomap.data.db.PhotoLocationEntity
import ny.photomap.data.db.toEntity
import ny.photomap.data.model.PhotoLocationData
import ny.photomap.domain.PhotoRepository
import ny.photomap.domain.Result
import ny.photomap.domain.model.PhotoLocationEntityModel
import ny.photomap.domain.model.PhotoLocationRequestModel
import ny.photomap.domain.runResultCatching
import javax.inject.Inject

class PhotoRepositoryImpl @Inject constructor(
    private val dataSource: PhotoDataSource,
    private val timeStamp: TimeStamp,
) : PhotoRepository {
    override suspend fun fetchAllPhotoLocation(): Result<List<PhotoLocationRequestModel>> {
        return runResultCatching {
            dataSource.fetchAllPhotoLocation().map(PhotoLocationData::toModel)
        }
    }

    override suspend fun fetchPhotoLocationAddedAfter(fetchTime: Long): Result<List<PhotoLocationRequestModel>> {
        return runResultCatching {
            dataSource.fetchPhotoLocationAddedAfter(fetchTime = fetchTime)
                .map(PhotoLocationData::toModel)
        }
    }

    override suspend fun saveLatestUpdateTime(): Result<Unit> = runResultCatching {
        dataSource.saveLatestUpdateTime(timeStamp.currentTime)
    }


    override suspend fun getLatestUpdateTime(): Result<Long> = runResultCatching {
        dataSource.getLatestUpdateTime()
    }


    override suspend fun isSyncExpired(days: Int): Result<Boolean> = runResultCatching {
        timeStamp.hasTimePassed(lastUpdateTime = dataSource.getLatestUpdateTime(), day = days)
    }

    override suspend fun saveAllPhotoLocation(list: List<PhotoLocationRequestModel>): Result<Unit> {
        return runResultCatching {
            dataSource.saveAllPhotoLocation(
                list.map(PhotoLocationRequestModel::toEntity)
            )
        }
    }

    override suspend fun deleteAllPhotoLocation(): Result<Unit> = runResultCatching {
        dataSource.deleteAllPhotoLocation()
    }

    override suspend fun getPhotoLocation(id: Long): Result<PhotoLocationEntityModel> {
        return runResultCatching {
            dataSource.getPhotoLocation(id = id).toModel()
        }
    }

    suspend fun getPhotoLocation(
        latitude: Double,
        longitude: Double,
        range: Double,
    ): Result<List<PhotoLocationEntityModel>> {
        return runResultCatching {
            dataSource.getPhotoLocation(
                latitude = latitude, longitude = longitude,
                range = range
            ).map(PhotoLocationEntity::toModel)
        }
    }

    suspend fun getPhotoLocation(
        latitude: Double,
        longitude: Double,
        range: Double,
        startTime: Long,
        endTime: Long,
    ): Result<List<PhotoLocationEntityModel>> {
        return runResultCatching {
            dataSource.getPhotoLocation(
                latitude = latitude, longitude = longitude,
                range = range, startTime = startTime, endTime = endTime
            ).map(PhotoLocationEntity::toModel)
        }
    }

    override suspend fun getPhotoLocation(
        latitude: Double,
        longitude: Double,
        range: Double,
        startTime: Long?,
        endTime: Long?,
    ): Result<List<PhotoLocationEntityModel>> {
        return if (startTime != null && endTime != null) {
            getPhotoLocation(
                latitude = latitude, longitude = longitude,
                range = range, startTime = startTime, endTime = endTime
            )
        } else {
            getPhotoLocation(
                latitude = latitude, longitude = longitude,
                range = range
            )
        }
    }

    suspend fun getPhotoLocation(
        northLatitude: Double,
        southLatitude: Double,
        eastLongitude: Double,
        westLongitude: Double,
    ): Result<List<PhotoLocationEntityModel>> {
        return runResultCatching {
            dataSource.getPhotoLocation(
                northLatitude = northLatitude, southLatitude = southLatitude,
                eastLongitude = eastLongitude, westLongitude = westLongitude,
            ).map(PhotoLocationEntity::toModel)
        }
    }

    suspend fun getPhotoLocation(
        northLatitude: Double,
        southLatitude: Double,
        eastLongitude: Double,
        westLongitude: Double,
        startTime: Long,
        endTime: Long,
    ): Result<List<PhotoLocationEntityModel>> {
        return runResultCatching {
            dataSource.getPhotoLocation(
                northLatitude = northLatitude, southLatitude = southLatitude,
                eastLongitude = eastLongitude, westLongitude = westLongitude,
                startTime = startTime, endTime = endTime
            ).map(PhotoLocationEntity::toModel)
        }
    }

    override suspend fun getPhotoLocation(
        northLatitude: Double,
        southLatitude: Double,
        eastLongitude: Double,
        westLongitude: Double,
        startTime: Long?,
        endTime: Long?,
    ): Result<List<PhotoLocationEntityModel>> {
        return if (startTime != null && endTime != null) {
            getPhotoLocation(
                northLatitude = northLatitude, southLatitude = southLatitude,
                eastLongitude = eastLongitude, westLongitude = westLongitude,
                startTime = startTime, endTime = endTime
            )
        } else {
            getPhotoLocation(
                northLatitude = northLatitude, southLatitude = southLatitude,
                eastLongitude = eastLongitude, westLongitude = westLongitude,
            )
        }

    }

    override suspend fun initializePhotoLocation(list: List<PhotoLocationRequestModel>): Result<Unit> {
        return runResultCatching {
            dataSource.initializePhotoLocation(list.map(PhotoLocationRequestModel::toEntity))
        }
    }

    override suspend fun getLatestPhotoLocation(): Result<PhotoLocationEntityModel?> {
        return runResultCatching {
            dataSource.getLatestPhotoLocation()?.toModel()
        }
    }

    override suspend fun getLocationText(
        latitude: Double,
        longitude: Double,
    ): String = dataSource.getLocationText(latitude = latitude, longitude = longitude)


    // todo 추후 작업
    /*override suspend fun getPhotoLocationWithOffset(
        latitude: Double,
        longitude: Double,
        range: Double,
        offset: Int,
        limit: Int,
    ): Result<List<PhotoLocationModel>> {
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
    ): Result<List<PhotoLocationModel>> {
        TODO("Not yet implemented")
    }*/
}