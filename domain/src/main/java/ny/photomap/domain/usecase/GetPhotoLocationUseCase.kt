package ny.photomap.domain.usecase

import ny.photomap.domain.PhotoRepository
import ny.photomap.domain.Result
import ny.photomap.domain.model.PhotoLocationModel
import javax.inject.Inject

class GetPhotoLocationUseCase @Inject constructor(
    private val repository: PhotoRepository,
) {
    suspend operator fun invoke(
        northLatitude: Double,
        southLatitude: Double,
        eastLongitude: Double,
        westLongitude: Double,
    ): Result<List<PhotoLocationModel>> {
        return repository.getPhotoLocation(
            northLatitude = northLatitude,
            southLatitude = southLatitude,
            eastLongitude = eastLongitude,
            westLongitude = westLongitude
        )
    }

}