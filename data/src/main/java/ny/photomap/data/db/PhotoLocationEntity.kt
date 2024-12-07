package ny.photomap.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "photo_location_table")
data class PhotoLocationEntity(
    val uri: String,
    val name: String?,
    val latitude: Double,
    val longitude: Double,
    val generatedTime: String?,
    val addedTime: String?,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    val thumbNail: ByteArray?,
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}