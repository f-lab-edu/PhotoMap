package ny.photomap.domain.usecase

import ny.photomap.domain.PhotoRepository
import javax.inject.Inject

class GetLocationTextUseCase @Inject constructor(
    private val repository: PhotoRepository,
) {
    suspend operator fun invoke(latitude: Double, longitude: Double): String =
        repository.getLocationText(
            latitude = latitude,
            longitude = longitude
        )
}