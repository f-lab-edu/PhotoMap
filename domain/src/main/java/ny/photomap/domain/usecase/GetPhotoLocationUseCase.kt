package ny.photomap.domain.usecase

import ny.photomap.domain.PhotoRepository
import ny.photomap.domain.Result
import ny.photomap.domain.usecase.CheckSyncStateUseCase.CheckSyncStatusUseCaseReturn
import javax.inject.Inject

class GetPhotoLocationUseCase @Inject constructor(
    private val repository: PhotoRepository,
) {
//    suspend operator fun invoke(): Result<> {
//        repository.getPhotoLocation()
//    }
}