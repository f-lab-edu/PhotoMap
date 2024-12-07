package ny.photomap.domain.model

data class PhotoLocationModel(
    val uriString: String,
    val name: String?,
    val latitude: Double,
    val longitude: Double,
    val generationTimeMillis: Long?,
    val additionTimeMillis: Long?,
    val thumbNail : ByteArray?
)
