package ny.photomap.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ny.photomap.data.TimeStampImpl
import ny.photomap.data.datasource.PhotoDataSource
import ny.photomap.data.datasource.PhotoDataSourceImpl
import ny.photomap.data.preferences.PhotoLocationPreferencesImpl
import ny.photomap.data.preferences.PhotoLocationReferences
import ny.photomap.data.repository.PhotoRepositoryImpl
import ny.photomap.domain.PhotoRepository
import ny.photomap.data.TimeStamp
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
interface BindSingletonModule {

    @Binds
    fun bindTimeStamp(
        timeStamp: TimeStampImpl,
    ): TimeStamp

    @Singleton
    @Binds
    fun bindPhotoDataSource(
        dataSource: PhotoDataSourceImpl,
    ): PhotoDataSource

    @Singleton
    @Binds
    fun bindPhotoLocationPreferences(preferences: PhotoLocationPreferencesImpl): PhotoLocationReferences

    @Singleton
    @Binds
    fun bindPhotoRepository(photoRepository: PhotoRepositoryImpl): PhotoRepository

}