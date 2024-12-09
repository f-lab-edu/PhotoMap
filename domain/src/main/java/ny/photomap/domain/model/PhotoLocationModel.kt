package ny.photomap.domain.model

data class PhotoLocationModel(
    val uriString: String,
    val name: String?,
    val latitude: Double,
    val longitude: Double,
    val generatedTimeMillis: Long,
    val addedTimeMillis: Long,
    val thumbNail : ByteArray?
)
