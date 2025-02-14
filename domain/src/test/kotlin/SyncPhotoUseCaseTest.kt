import kotlinx.coroutines.test.runTest
import ny.photomap.domain.PhotoRepository
import ny.photomap.domain.Result
import ny.photomap.domain.usecase.SyncPhotoUseCase
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class SyncPhotoUseCaseTest {

    private val repository: PhotoRepository = mock()

    private lateinit var useCase: SyncPhotoUseCase

    @BeforeEach
    fun setUp() {
        useCase = SyncPhotoUseCase(
            repository = repository
        )
    }

    @Test
    @DisplayName("사진 업데이트 성공")
    fun syncSuccess() = runTest {
        whenever(repository.fetchAllPhotoLocation()).thenReturn(Result.Success(mockModelList))
        whenever(repository.initializePhotoLocation(mockModelList)).thenReturn(Result.Success(Unit))

        val result = useCase()

        assertEquals(Result.Success(Unit), result)
        verify(repository).fetchAllPhotoLocation()
        verify(repository).initializePhotoLocation(mockModelList)
        verify(repository).saveLatestUpdateTime()
    }

    @Test
    @DisplayName("단말에서 사진 조회는 성공하나 사진 정보 업데이트에 실패")
    fun syncFailInUpdateStep() = runTest {
        whenever(repository.fetchAllPhotoLocation()).thenReturn(Result.Success(mockModelList))
        val throwable = Throwable("사진 업데이트 실패")
        whenever(repository.initializePhotoLocation(mockModelList)).thenReturn(
            Result.Failure(
                throwable
            )
        )

        val result = useCase()
        assertEquals(Result.Failure(throwable), result)
        verify(repository).fetchAllPhotoLocation()
        verify(repository).initializePhotoLocation(mockModelList)
        verify(repository, never()).saveLatestUpdateTime()
    }

    @Test
    @DisplayName("단말에서 사진 조회부터 실패")
    fun syncFailInFetchStep() = runTest {
        val throwable = Throwable("단말에서 사진 조회 실패")
        whenever(repository.fetchAllPhotoLocation()).thenReturn(Result.Failure(throwable))

        val result = useCase()

        assertEquals(Result.Failure(throwable), result)
        verify(repository).fetchAllPhotoLocation()
        verify(repository, never()).initializePhotoLocation(any())
        verify(repository, never()).saveLatestUpdateTime()
    }
}