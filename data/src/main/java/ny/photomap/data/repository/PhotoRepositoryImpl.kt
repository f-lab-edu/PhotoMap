package ny.photomap.data.repository

import ny.photomap.data.datasource.PhotoDataSource
import ny.photomap.domain.PhotoRepository
import ny.photomap.domain.model.PhotoLocationModel
import javax.inject.Inject

class PhotoRepositoryImpl @Inject constructor(private val dataSource: PhotoDataSource) :
    PhotoRepository {
    override suspend fun fetchAllPhotoLocation(): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun fetchPhotoLocationAddedAfter(fetchTime: Long): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun saveLatestFetchTime(fetchTime: Long): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun getLatestFetchTime(): Result<Long> {
        TODO("Not yet implemented")
    }

    override suspend fun getPhotoLocation(
        latitude: Double,
        longitude: Double,
        range: Double,
    ): Result<List<PhotoLocationModel>> {
        TODO("Not yet implemented")
    }

    override suspend fun getPhotoLocation(
        latitude: Double,
        longitude: Double,
        range: Double,
        startTime: Long,
        endTime: Long,
    ): Result<List<PhotoLocationModel>> {
        TODO("Not yet implemented")
    }

    override suspend fun getPhotoLocationWithOffset(
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
    }
}