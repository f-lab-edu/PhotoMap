package ny.photomap.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ny.photomap.ui.navigation.Destination
import ny.photomap.ui.navigation.Navigator
import ny.photomap.ui.navigation.PhotoMapNavigator
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
object ProvideSingletonModule {

    @Singleton
    @Provides
    fun provideNavigator(): Navigator = PhotoMapNavigator(startDestination = Destination.MainGraph)
}