package ny.photomap.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ny.photomap.data.repository.PhotoRepositoryImpl
import ny.photomap.domain.PhotoRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindPhotoRepository(photoRepository: PhotoRepositoryImpl): PhotoRepository
}