package ny.photomap.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface PhotoLocationDao {

    @Query("SELECT * FROM photo_location_table")
    suspend fun getAll(): List<PhotoLocationEntity>

    @Query("SELECT * FROM photo_location_table WHERE id = :id")
    suspend fun getLocation(id: Long) : PhotoLocationEntity

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
        """SELECT * FROM photo_location_table 
            WHERE (latitude BETWEEN :southLatitude AND :northLatitude)
            AND (
                (:westLongitude <= :eastLongitude AND longitude BETWEEN :westLongitude AND :eastLongitude)
                OR (
                    :westLongitude > :eastLongitude AND
                    (longitude BETWEEN :westLongitude AND 180.0 OR longitude BETWEEN -180.0 AND :eastLongitude)
                )
            )
        """
    )
    suspend fun getLocationOf(
        northLatitude: Double,
        southLatitude: Double,
        eastLongitude: Double,
        westLongitude: Double,
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
        """SELECT * FROM photo_location_table 
            WHERE (latitude BETWEEN :southLatitude AND :northLatitude)
            AND (
                (:westLongitude <= :eastLongitude AND longitude BETWEEN :westLongitude AND :eastLongitude)
                OR (
                    :westLongitude > :eastLongitude AND
                    (longitude BETWEEN :westLongitude AND 180.0 OR longitude BETWEEN -180.0 AND :eastLongitude)
                )
            ) AND (generatedTime BETWEEN :fromTime AND :toTime)
        """
    )
    suspend fun getLocationAndDateOf(
        northLatitude: Double,
        southLatitude: Double,
        eastLongitude: Double,
        westLongitude: Double,
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

    @Transaction
    suspend fun initialize(entityList: List<PhotoLocationEntity>) {
        deleteAll()
        insertAll(entityList)
    }

    @Query("SELECT * FROM photo_location_table ORDER BY generatedTime DESC, addedTime DESC LIMIT 1")
    suspend fun getLatest() : PhotoLocationEntity?

}