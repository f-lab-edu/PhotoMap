package ny.photomap.domain.usecase

import ny.photomap.domain.PhotoRepository
import ny.photomap.domain.Result
import ny.photomap.domain.suspendFlatMap
import javax.inject.Inject

class SyncPhotoUseCase @Inject constructor(
    private val repository: PhotoRepository,
) {
    suspend operator fun invoke(): Result<Unit> {
        return repository.fetchAllPhotoLocation().suspendFlatMap { list ->
            when (val result = repository.initializePhotoLocation(list)) {
                is Result.Success -> Result.Success(Unit)
                else -> Result.Failure(result.throwableOrNull())
            }
        }
    }
}