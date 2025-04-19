package ny.photomap.data.di

import android.content.ContentResolver
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ny.photomap.data.db.PhotoLocationDao
import ny.photomap.data.db.PhotoLocationDatabase
import ny.photomap.data.preferences.PhotoLocationPreferencesImpl.Companion.NAME_PHOTO_LOCATION_PREFERENCES_DATASTORE
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object ProvideSingletonModule {

    @Provides
    fun providePhotoLocationDatabase(@ApplicationContext context: Context): PhotoLocationDatabase =
        PhotoLocationDatabase.getInstance(context)

    @Provides
    fun providePhotoLocationDao(photoLocationDatabase: PhotoLocationDatabase): PhotoLocationDao =
        photoLocationDatabase.photoLocationDao()

    @Provides
    fun provideContentResolver(@ApplicationContext context: Context): ContentResolver =
        context.contentResolver

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