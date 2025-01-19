package ny.photomap.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import ny.photomap.data.model.ModelMapper
import ny.photomap.domain.model.PhotoLocationRequestModel
import ny.photomap.domain.model.PhotoLocationEntityModel

@Entity(tableName = "photo_location_table")
data class PhotoLocationEntity(
    val uri: String,
    val name: String?,
    val latitude: Double,
    val longitude: Double,
    val generatedTime: Long,
    val addedTime: Long,
) : ModelMapper<PhotoLocationEntityModel> {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    override fun toModel(): PhotoLocationEntityModel = PhotoLocationEntityModel(
        id = id,
        uri = uri,
        name = name,
        latitude = latitude,
        longitude = longitude,
        generatedTimeMillis = generatedTime,
        addedTimeMillis = addedTime,
    )
}

fun PhotoLocationEntityModel.toEntity(): PhotoLocationEntity = PhotoLocationEntity(
    uri = uri,
    name = name,
    latitude = latitude,
    longitude = longitude,
    generatedTime = generatedTimeMillis,
    addedTime = addedTimeMillis,
)

fun PhotoLocationRequestModel.toEntity(): PhotoLocationEntity = PhotoLocationEntity(
    uri = uri,
    name = name,
    latitude = latitude,
    longitude = longitude,
    generatedTime = generatedTimeMillis,
    addedTime = addedTimeMillis,
)