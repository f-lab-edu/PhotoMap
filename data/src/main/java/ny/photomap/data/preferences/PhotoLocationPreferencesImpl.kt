package ny.photomap.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PhotoLocationPreferencesImpl(private val dataStore: DataStore<Preferences>) :
    PhotoLocationReferences {

    private val keyTimeSyncDatabase = longPreferencesKey(PREFERENCES_KEY_TIME_SYNC_DATABASE)

    override val timeSyncDatabaseFlow: Flow<Long> = dataStore.data.map { preferences ->
        preferences[keyTimeSyncDatabase] ?: 0L
    }

    override suspend fun updateTimeSyncDatabase(timeMills: Long) {
        dataStore.edit { preferences ->
            preferences[keyTimeSyncDatabase] = timeMills
        }
    }

    companion object {
        const val NAME_PHOTO_LOCATION_PREFERENCES_DATASTORE = "photo_location_preferences"
        const val PREFERENCES_KEY_TIME_SYNC_DATABASE = "time_sync_database"
    }
}