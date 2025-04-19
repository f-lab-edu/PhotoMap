package ny.photomap.ui.marker

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.algo.NonHierarchicalViewBasedAlgorithm
import com.google.maps.android.compose.GoogleMapComposable
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.clustering.Clustering
import com.google.maps.android.compose.clustering.rememberClusterManager
import com.google.maps.android.compose.clustering.rememberClusterRenderer
import ny.photomap.R
import ny.photomap.model.PhotoLocationUIModel
import timber.log.Timber

@OptIn(MapsComposeExperimentalApi::class)
@GoogleMapComposable
@Composable
fun PhotoLocationClustering(
    items: List<PhotoLocationUIModel>,
    onPhotoItemClick: (itemId: Long) -> Unit,
    onPhotoClusteringClick: (item: LatLng, clusteringList: Array<PhotoLocationUIModel>) -> Unit,
) {

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp
    val clusterManager = rememberClusterManager<PhotoLocationUIModel>()

    clusterManager?.setAlgorithm(
        NonHierarchicalViewBasedAlgorithm(
            screenWidth.value.toInt(),
            screenHeight.value.toInt()
        )
    )
    val renderer = rememberClusterRenderer(
        clusterContent = { cluster ->
            if (!cluster.items.isNullOrEmpty()) {
                PhotoLocationMarker(
                    modifier = Modifier.size(dimensionResource(R.dimen.size_thumbnail)),
                    text = "${cluster.size}",
                    model = cluster.getItems().last()
                )
            }
        },
        clusterItemContent = { item ->
            PhotoLocationMarker(
                modifier = Modifier.size(dimensionResource(R.dimen.size_thumbnail)),
                text = "",
                model = item,
            )
        },
        clusterManager = clusterManager,
    )

    SideEffect {
        clusterManager ?: return@SideEffect
        clusterManager.setOnClusterClickListener {
            Timber.d("Cluster clicked! $it")
            onPhotoClusteringClick(it.position, it.items.toTypedArray())
            false
        }

        clusterManager.setOnClusterItemClickListener {
            Timber.d("사진 아이템 클릭 $it")
            onPhotoItemClick(it.id)
            false
        }
        clusterManager.setOnClusterItemInfoWindowClickListener {
            Timber.d("Cluster item info window clicked! $it")
        }
    }
    SideEffect {
        if (clusterManager?.renderer != renderer) {
            clusterManager?.renderer = renderer ?: return@SideEffect
        }
    }

    if (clusterManager != null) {
        Clustering(
            items = items,
            clusterManager = clusterManager,
        )
    }
}