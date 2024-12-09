package ny.photomap.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ny.photomap.data.model.ModelMapper
import ny.photomap.domain.model.PhotoLocationModel

@Entity(tableName = "photo_location_table")
data class PhotoLocationEntity(
    val uri: String,
    val name: String?,
    val latitude: Double,
    val longitude: Double,
    val generatedTime: Long,
    val addedTime: Long,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    val thumbNail: ByteArray?
) : ModelMapper<PhotoLocationModel> {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    override fun toModel(): PhotoLocationModel = PhotoLocationModel(
        uriString = uri.toString(),
        name = name,
        latitude = latitude,
        longitude = longitude,
        generatedTimeMillis = generatedTime,
        addedTimeMillis = addedTime,
        thumbNail = thumbNail
    )
}