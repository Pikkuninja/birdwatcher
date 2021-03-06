package fi.jara.birdwatcher.common.di.application

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import fi.jara.birdwatcher.data.ObservationRepository
import fi.jara.birdwatcher.data.room.RoomObservationRepository
import fi.jara.birdwatcher.data.room.ObservationDatabase
import fi.jara.birdwatcher.data.room.ObservationDatabaseMigration_2_to_3
import javax.inject.Singleton

@Module
class RepositoryModule {
    @Singleton
    @Provides
    fun provideObservationRepository(context: Context): ObservationRepository {
        val database = Room.databaseBuilder(context, ObservationDatabase::class.java, "observations.sqlite")
            .addMigrations(ObservationDatabaseMigration_2_to_3())
            .build()
        return RoomObservationRepository(database)
    }
}