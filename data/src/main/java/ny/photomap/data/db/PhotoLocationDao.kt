package ny.photomap.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PhotoLocationDao {

    @Query("SELECT * FROM photo_location_table")
    suspend fun getAll(): List<PhotoLocationEntity>

    @Query(
        "SELECT * FROM photo_location_table WHERE (latitude BETWEEN :latitude - :range AND :latitude + :range) " +
                "AND (longitude BETWEEN :longitude - :range AND :longitude + :range)"
    )
    suspend fun getLocationOf(
        latitude: Double,
        longitude: Double,
        range: Double,
    ): List<PhotoLocationEntity>

    @Query(
        "SELECT * FROM photo_location_table WHERE (latitude BETWEEN :latitude - :range AND :latitude + :range) " +
                "AND (longitude BETWEEN :longitude - :range AND :longitude + :range) " +
                "AND (generatedTime BETWEEN :fromTime AND :toTime)"
    )
    suspend fun getLocationAndDateOf(
        latitude: Double,
        longitude: Double,
        range: Double,
        fromTime: Long,
        toTime: Long,
    ): List<PhotoLocationEntity>

    @Query(
        "SELECT COUNT(*) FROM photo_location_table WHERE (latitude BETWEEN :latitude - :range AND :latitude + :range) " +
                "AND (longitude BETWEEN :longitude - :range AND :longitude + :range) " +
                "AND (generatedTime BETWEEN :fromTime AND :toTime)"
    )
    suspend fun getCountOfLocationAndDate(
        latitude: Double,
        longitude: Double,
        range: Double,
        fromTime: Long,
        toTime: Long,
    ): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: PhotoLocationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entityList: List<PhotoLocationEntity>)

    @Query("DELETE FROM photo_location_table")
    suspend fun deleteAll()

}