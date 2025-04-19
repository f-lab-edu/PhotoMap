package ny.photomap.domain.model

data class PhotoLocationRequestModel(
    val uri: String,
    val name: String?,
    val latitude: Double,
    val longitude: Double,
    val generatedTimeMillis: Long,
    val addedTimeMillis: Long,
)

data class PhotoLocationEntityModel(
    val id: Long,
    val uri: String,
    val name: String?,
    val latitude: Double,
    val longitude: Double,
    val generatedTimeMillis: Long,
    val addedTimeMillis: Long,
    val location: String?
)