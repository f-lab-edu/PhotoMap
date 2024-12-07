package ny.photomap.data.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ny.photomap.data.db.PhotoLocationDao
import ny.photomap.data.db.PhotoLocationDatabase

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {
    @Provides
    fun providePhotoLocationDatabase(@ApplicationContext context: Context): PhotoLocationDatabase =
        PhotoLocationDatabase.getInstance(context)

    @Provides
    fun providePhotoLocationDao(photoLocationDatabase: PhotoLocationDatabase): PhotoLocationDao =
        photoLocationDatabase.photoLocationDao()
}