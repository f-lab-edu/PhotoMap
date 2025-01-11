package ny.photomap.domain.usecase

import ny.photomap.domain.PhotoRepository
import ny.photomap.domain.Result
import ny.photomap.domain.model.PhotoLocationModel
import javax.inject.Inject

class GetLatestPhotoLocationUseCase @Inject constructor(
    private val repository: PhotoRepository,
) {
    suspend operator fun invoke(): Result<PhotoLocationModel?> {
        return repository.getLatestPhotoLocation()
    }
}