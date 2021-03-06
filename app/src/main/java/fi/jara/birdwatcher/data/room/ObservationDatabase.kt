package fi.jara.birdwatcher.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters


@Database(
    entities = [ObservationEntity::class],
    version = 3,
    exportSchema = true)
@TypeConverters(RoomTypeConverters::class)
abstract class ObservationDatabase: RoomDatabase() {
    abstract fun observationDao(): ObservationDao
}