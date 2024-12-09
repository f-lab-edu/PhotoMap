package ny.photomap.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

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

    @Insert
    suspend fun insert(entity: PhotoLocationEntity)

    @Insert
    suspend fun insertAll(entityList: List<PhotoLocationEntity>)

    @Update
    suspend fun update(entity: PhotoLocationEntity)

    @Delete
    suspend fun delete(entity: PhotoLocationEntity)

    @Query("DELETE FROM photo_location_table")
    suspend fun deleteAll()

}