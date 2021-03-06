package fi.jara.birdwatcher.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/*
    As Room supports parameters only in the same way that SQLites compiled statements do, an order by column name
    can't be substituted with a parameter. Result: this mess... Maybe switch to raw queries?
 */

@Dao
interface ObservationDao {
    @Query("SELECT * FROM observations ORDER BY species DESC")
    fun observeAllObservationsSpeciesDesc(): Flow<List<ObservationEntity>>

    @Query("SELECT * FROM observations ORDER BY species ASC")
    fun observeAllObservationsSpeciesAsc(): Flow<List<ObservationEntity>>

    @Query("SELECT * FROM observations ORDER BY timestamp DESC")
    fun observeAllObservationsTimestampDesc(): Flow<List<ObservationEntity>>

    @Query("SELECT * FROM observations ORDER BY timestamp ASC")
    fun observeAllObservationsTimestampAsc(): Flow<List<ObservationEntity>>

    @Query("SELECT * FROM observations WHERE id = :id")
    fun observeObservation(id: Long): Flow<ObservationEntity?>

    @Insert
    fun insertObservation(observation: ObservationEntity): Long
}