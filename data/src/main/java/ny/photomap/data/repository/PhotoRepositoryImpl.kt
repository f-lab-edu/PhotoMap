package ny.photomap.data.repository

import ny.photomap.data.datasource.PhotoDataSource
import ny.photomap.data.db.PhotoLocationEntity
import ny.photomap.data.db.toEntity
import ny.photomap.data.model.PhotoLocationData
import ny.photomap.domain.PhotoRepository
import ny.photomap.domain.Result
import ny.photomap.domain.model.PhotoLocationModel
import ny.photomap.domain.runResultCatching
import javax.inject.Inject

class PhotoRepositoryImpl @Inject constructor(private val dataSource: PhotoDataSource) :
    PhotoRepository {
    override suspend fun fetchAllPhotoLocation(): Result<List<PhotoLocationModel>> {
        return runResultCatching {
            dataSource.fetchAllPhotoLocation().map(PhotoLocationData::toModel)
        }
    }

    override suspend fun fetchPhotoLocationAddedAfter(fetchTime: Long): Result<List<PhotoLocationModel>> {
        return runResultCatching {
            dataSource.fetchPhotoLocationAddedAfter(fetchTime = fetchTime)
                .map(PhotoLocationData::toModel)
        }
    }

    override suspend fun saveLatestFetchTime(fetchTime: Long): Result<Unit> = runResultCatching {
        dataSource.saveLatestFetchTime(fetchTime)

    }


    override suspend fun getLatestFetchTime(): Result<Long> = runResultCatching {
        dataSource.getLatestFetchTime()
    }

    override suspend fun saveAllPhotoLocation(list: List<PhotoLocationModel>): Result<Unit> {
        return runResultCatching {
            dataSource.saveAllPhotoLocation(
                list.map(PhotoLocationModel::toEntity)
            )
        }
    }

    override suspend fun deleteAllPhotoLocation(): Result<Unit> = runResultCatching {
        dataSource.deleteAllPhotoLocation()
    }

    override suspend fun getPhotoLocation(
        latitude: Double,
        longitude: Double,
        range: Double,
    ): Result<List<PhotoLocationModel>> {
        return runResultCatching {
            dataSource.getPhotoLocation(
                latitude = latitude, longitude = longitude,
                range = range
            ).map(PhotoLocationEntity::toModel)
        }
    }

    override suspend fun getPhotoLocation(
        latitude: Double,
        longitude: Double,
        range: Double,
        startTime: Long,
        endTime: Long,
    ): Result<List<PhotoLocationModel>> {
        return runResultCatching {
            dataSource.getPhotoLocation(
                latitude = latitude, longitude = longitude,
                range = range, startTime = startTime, endTime = endTime
            ).map(PhotoLocationEntity::toModel)
        }
    }

    override suspend fun initializePhotoLocation(list: List<PhotoLocationModel>): Result<Unit> {
        return runResultCatching {
            dataSource.initializePhotoLocation(list.map(PhotoLocationModel::toEntity))
        }
    }

    override suspend fun getLatestPhotoLocation(): Result<PhotoLocationModel?> {
        return runResultCatching {
            dataSource.getLatestPhotoLocation()?.toModel()
        }
    }

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