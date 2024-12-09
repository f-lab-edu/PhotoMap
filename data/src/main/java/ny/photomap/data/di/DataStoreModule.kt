package ny.photomap.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ny.photomap.data.preferences.PhotoLocationPreferencesImpl
import ny.photomap.data.preferences.PhotoLocationPreferencesImpl.Companion.NAME_PHOTO_LOCATION_PREFERENCES_DATASTORE
import ny.photomap.data.preferences.PhotoLocationReferences
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataStoreModule {

    @Singleton
    @Binds
    fun bindPhotoLocationPreferences(photoLocationPreferences: PhotoLocationPreferencesImpl): PhotoLocationReferences

    companion object {
        @Singleton
        @Provides
        fun providePreferencesDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
            return PreferenceDataStoreFactory.create(
                corruptionHandler = ReplaceFileCorruptionHandler(
                    produceNewData = { emptyPreferences() }
                ),
                produceFile = {
                    context.preferencesDataStoreFile(
                        NAME_PHOTO_LOCATION_PREFERENCES_DATASTORE
                    )
                }
            )
        }
    }
}