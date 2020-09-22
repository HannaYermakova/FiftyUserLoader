package by.aermakova.fiftyusersloader.data.di

import android.content.Context
import androidx.room.Room
import by.aermakova.fiftyusersloader.data.UserInteractor
import by.aermakova.fiftyusersloader.data.local.UserDao
import by.aermakova.fiftyusersloader.data.local.UserLocalDataBase
import by.aermakova.fiftyusersloader.data.local.UserLocalRepository
import by.aermakova.fiftyusersloader.data.remote.UserApiClient
import by.aermakova.fiftyusersloader.data.remote.UserGeneratorApi
import by.aermakova.fiftyusersloader.data.remote.UserRemoteRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideUserDao(
        localDB: UserLocalDataBase
    ): UserDao {
        return localDB.usersDao()
    }

    @Provides
    @Singleton
    fun provideUserInteractor(
        localDB: UserLocalRepository,
        remoteDB: UserRemoteRepository
    ): UserInteractor {
        return UserInteractor(localDB, remoteDB)
    }

    @Singleton
    @Provides
    fun provideUserApiClient(): UserApiClient {
        return UserApiClient()
    }

    @Singleton
    @Provides
    fun provideUserGeneratorApi(userApiClient: UserApiClient): UserGeneratorApi {
        return userApiClient.getUserApiService()
    }

    @Singleton
    @Provides
    fun provideUserRemoteRepository(userGeneratorApi: UserGeneratorApi): UserRemoteRepository {
        return UserRemoteRepository(userGeneratorApi)
    }

    @Singleton
    @Provides
    fun provideUserLocalRepository(userDto: UserDao): UserLocalRepository {
        return UserLocalRepository(userDto)
    }

    @Provides
    @Singleton
    fun providesUserDataBase(@ApplicationContext appContext: Context): UserLocalDataBase {
        return Room.databaseBuilder(
            appContext,
            UserLocalDataBase::class.java,
            USER_TABLE_NAME
        ).build()
    }
    const val USER_TABLE_NAME = "user_table"
}