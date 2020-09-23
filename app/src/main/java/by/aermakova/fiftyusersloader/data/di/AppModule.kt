package by.aermakova.fiftyusersloader.data.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import by.aermakova.fiftyusersloader.data.UserInteractor
import by.aermakova.fiftyusersloader.data.local.USER_TABLE_NAME
import by.aermakova.fiftyusersloader.data.local.UserDao
import by.aermakova.fiftyusersloader.data.local.UserLocalDataBase
import by.aermakova.fiftyusersloader.data.local.UserLocalRepository
import by.aermakova.fiftyusersloader.data.remote.*
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
    fun provideFileIoClient(): FileIoClient {
        return FileIoClient()
    }

    @Singleton
    @Provides
    fun provideFileUploadApi(fileIoClient: FileIoClient): FileUploadApi {
        return fileIoClient.getFileUploadApi()
    }

    @Singleton
    @Provides
    fun provideUserGeneratorApi(userApiClient: UserApiClient): UserGeneratorApi {
        return userApiClient.getUserApiService()
    }

    @Singleton
    @Provides
    fun provideUserRemoteRepository(
        userGeneratorApi: UserGeneratorApi,
        fileUploadApi: FileUploadApi
    ): UserRemoteRepository {
        return UserRemoteRepository(userGeneratorApi, fileUploadApi)
    }

    @Singleton
    @Provides
    fun provideUserLocalRepository(userDto: UserDao): UserLocalRepository {
        return UserLocalRepository(userDto)
    }

    @Provides
    @Singleton
    fun providesApplication(@ApplicationContext appContext: Context): Context {
        return appContext
    }

    @Provides
    @Singleton
    fun providesUserDataBase(@ApplicationContext appContext: Context): UserLocalDataBase {
        return Room.databaseBuilder(
            appContext,
            UserLocalDataBase::class.java,
            USER_TABLE_NAME
        ).addMigrations(MIGRATION_1_2)
            .build()
    }
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE user_table ADD COLUMN version INTEGER")
    }
}