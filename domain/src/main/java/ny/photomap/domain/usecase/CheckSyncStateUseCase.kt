package ny.photomap.domain.usecase

import ny.photomap.domain.PhotoRepository
import ny.photomap.domain.Result
import ny.photomap.domain.TimeStamp
import javax.inject.Inject

class CheckSyncStateUseCase @Inject constructor(
    private val repository: PhotoRepository,
    private val timeStamp: TimeStamp,
) {
    suspend operator fun invoke(): Result<CheckSyncStatusUseCaseReturn> {
        val time = repository.getLatestUpdateTime().getOrNull()

        val shouldSync = when (time) {
            0L -> true
            null -> false
            else -> timeStamp.hasTimePassed(
                lastUpdateTime = time,
                day = 7
            )
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
