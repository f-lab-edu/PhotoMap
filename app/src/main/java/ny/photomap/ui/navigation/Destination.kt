package ny.photomap.ui.navigation
import kotlinx.serialization.Serializable


sealed interface Destination {
    @Serializable
    data object MainGraph : Destination

    @Serializable
    data object PhotoMap : Destination

    @Serializable
    data class Photo(val dataId : Long) : Destination

    @Serializable
    data object SettingGraph : Destination

    @Serializable
    data object Setting : Destination
}
