package ny.photomap.data.preferences

import kotlinx.coroutines.flow.Flow

interface PhotoLocationReferences {

    /**
     * 데이터 베이스 싱크 맞춘 시점 정보
     */
    val timeSyncDatabaseFlow: Flow<Long>

    suspend fun updateTimeSyncDatabase(timeMills: Long)
}