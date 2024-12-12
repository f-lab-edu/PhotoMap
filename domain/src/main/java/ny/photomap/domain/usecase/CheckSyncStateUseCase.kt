package ny.photomap.domain.usecase

import ny.photomap.domain.PhotoRepository
import ny.photomap.domain.Result
import javax.inject.Inject

class CheckSyncStateUseCase @Inject constructor(
    private val repository: PhotoRepository,
) {
    suspend operator fun invoke(): Result<CheckSyncStatusUseCaseReturn> {
        val time = repository.getLatestFetchTime().getOrNull()
        val shouldSync = when (time) {
            0L -> true
            null -> false
            else -> true // todo 싱크 시간 비교해서 일정 날짜가 지나면 진행하는 로직
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