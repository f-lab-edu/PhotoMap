package ny.photomap.domain.usecase

import ny.photomap.domain.PhotoRepository
import ny.photomap.domain.Result
import javax.inject.Inject

class CheckSyncStateUseCase @Inject constructor(
    private val repository: PhotoRepository,
) {
    suspend operator fun invoke(): Result<CheckSyncStatusUseCaseReturn> {

        val time = repository.getLatestUpdateTime().getOrNull()

        val shouldSync = when (time) {
            0L -> true
            else -> repository.isSyncExpired(7).getOrNull() == true
        }

        return Result.Success(
            CheckSyncStatusUseCaseReturn(
                shouldSync = shouldSync,
                lastSyncTime = time
            )
        )
    }

    data class CheckSyncStatusUseCaseReturn(val shouldSync: Boolean, val lastSyncTime: Long?)

}
