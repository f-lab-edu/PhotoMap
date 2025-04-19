package ny.photomap.data

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import ny.photomap.data.db.PhotoLocationDatabase
import ny.photomap.data.di.ProvideSingletonModule

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [ProvideSingletonModule::class]
)
class TestDatabaseModule {
    @Provides
    fun providePhotoLocationDatabase(@ApplicationContext context: Context): PhotoLocationDatabase =
        Room.inMemoryDatabaseBuilder(
            context,
            PhotoLocationDatabase::class.java,
        ).build()
}