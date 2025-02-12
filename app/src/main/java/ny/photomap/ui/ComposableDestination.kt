package ny.photomap.ui
import kotlinx.serialization.Serializable

@Serializable
sealed class Destination {
    @Serializable
    object PhotoMap : Destination()
    @Serializable
    data class Photo(val dataId : Long) : Destination()
}
