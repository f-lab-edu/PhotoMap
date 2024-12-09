package ny.photomap.data.preferences

import kotlinx.coroutines.flow.Flow

interface PhotoLocationReferences {

    val timeSyncDatabaseFlow: Flow<Long>
    suspend fun updateTimeSyncDatabase(timeMills: Long)
}