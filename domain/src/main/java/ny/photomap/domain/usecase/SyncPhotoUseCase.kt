package ny.photomap.domain.usecase

import ny.photomap.domain.PhotoRepository
import ny.photomap.domain.Result
import ny.photomap.domain.suspendFlatMap
import javax.inject.Inject

class SyncPhotoUseCase @Inject constructor(
    private val repository: PhotoRepository,
) {

    suspend operator fun invoke(): Result<SyncPhotoUseCaseReturn> {
        return repository.getLatestFetchTime().suspendFlatMap { time ->
            when (time == 0L) {
                true -> {
                    repository.fetchAllPhotoLocation().suspendFlatMap { list ->
                        when (repository.saveAllPhotoLocation(list)) {
                            is Result.Success -> Result.Success(SyncPhotoUseCaseReturn(true))
                            else -> Result.Failure(null)
                        }
                    }
                }

                false -> Result.Success(SyncPhotoUseCaseReturn(false))
            }
        }
    }

    @JvmInline
    value class SyncPhotoUseCaseReturn(val syncDone: Boolean)

}