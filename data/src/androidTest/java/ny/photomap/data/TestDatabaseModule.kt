package ny.photomap.data

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import ny.photomap.data.db.PhotoLocationDatabase
import ny.photomap.data.di.DatabaseModule

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DatabaseModule::class]
)
class TestDatabaseModule {
    @Provides
    fun providePhotoLocationDatabase(@ApplicationContext context: Context): PhotoLocationDatabase =
        PhotoLocationDatabase.getInstance(context)
}