package ny.photomap.domain.usecase

import ny.photomap.domain.PhotoRepository
import ny.photomap.domain.Result
import ny.photomap.domain.model.PhotoLocationEntityModel
import javax.inject.Inject

class GetPhotoLocationsInBoundaryUseCase @Inject constructor(
    private val repository: PhotoRepository,
) {
    suspend operator fun invoke(
        northLatitude: Double,
        southLatitude: Double,
        eastLongitude: Double,
        westLongitude: Double,
    ): Result<List<PhotoLocationEntityModel>> {
        return repository.getPhotoLocation(
            northLatitude = northLatitude,
            southLatitude = southLatitude,
            eastLongitude = eastLongitude,
            westLongitude = westLongitude
        )
    }

}