package ny.photomap.domain.model

data class PhotoLocationModel(
    val uri: String,
    val name: String?,
    val latitude: Double,
    val longitude: Double,
    val generatedTimeMillis: Long,
    val addedTimeMillis: Long,
//    val thumbNail : ByteArray
)