import kotlinx.coroutines.test.runTest
import ny.photomap.domain.PhotoRepository
import ny.photomap.domain.Result
import ny.photomap.domain.usecase.CheckSyncStateUseCase
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever

class CheckSyncStateUseCaseTest {

    private val repository: PhotoRepository = mock()


    private lateinit var useCase: CheckSyncStateUseCase

    @BeforeEach
    fun setUp() {
        useCase = CheckSyncStateUseCase(
            repository = repository
        )
    }

    @Test
    @DisplayName("한 번도 싱크 업데이트한 기록이 없을 때의 테스트")
    fun neverHaveUpdated() = runTest {
        whenever(repository.getLatestUpdateTime()).thenReturn(Result.Success(0L))

        val result = useCase()

        assertTrue(result is Result.Success)
        assertEquals(true, (result as Result.Success).data.shouldSync)
        assertEquals(0L, result.data.lastSyncTime)
    }

    @Test
    @DisplayName("아직 업데이트할 기간이 아닐 경우")
    fun doNotHaveToUpdated() = runTest {
        val mockTime = 1738135677000
        whenever(repository.getLatestUpdateTime()).thenReturn(Result.Success(mockTime))
        whenever(repository.isSyncExpired(7)).thenReturn(Result.Success(false))

        val result = useCase()

        assertTrue(result is Result.Success)
        assertEquals(false, (result as Result.Success).data.shouldSync)
        assertEquals(mockTime, result.data.lastSyncTime)
    }

    @Test
    @DisplayName("업데이트 기간이 지난 경우")
    fun shouldUpdated() = runTest {
        val mockTime = 1738135677000
        whenever(repository.getLatestUpdateTime()).thenReturn(Result.Success(mockTime))
        whenever(repository.isSyncExpired(7)).thenReturn(Result.Success(true))

        val result = useCase()

        assertTrue(result is Result.Success)
        assertEquals(true, (result as Result.Success).data.shouldSync)
        assertEquals(mockTime, result.data.lastSyncTime)
    }

    @Test
    @DisplayName("최신 업데이트 기간 조회 때 에러 발생 테스트")
    fun errorThrown() = runTest {
        whenever(repository.getLatestUpdateTime()).thenReturn(Result.Failure(Throwable("테스트용 발생 Throwable")))
        whenever(repository.isSyncExpired(7)).thenReturn(Result.Failure(Throwable("테스트용 발생 Throwable")))

        val result = useCase()

        assertTrue(result is Result.Success)
        assertEquals(false, (result as Result.Success).data.shouldSync)
        assertEquals(null, result.data.lastSyncTime)
    }


}