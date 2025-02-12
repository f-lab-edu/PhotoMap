package ny.photomap.domain.usecase

import ny.photomap.domain.PhotoRepository
import ny.photomap.domain.model.PhotoLocationEntityModel
import javax.inject.Inject
import ny.photomap.domain.Result

class GetPhotoLocationUseCase @Inject constructor(
    private val repository: PhotoRepository,
) {
    suspend operator fun invoke(id: Long): Result<PhotoLocationEntityModel> =
        repository.getPhotoLocation(id = id)

}