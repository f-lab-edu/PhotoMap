package ny.photomap.ui.marker

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
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
    onPhotoClick: (PhotoLocationUIModel) -> Unit,
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
            false
        }

        clusterManager.setOnClusterItemClickListener {
            Timber.d("사진 아이템 클릭 $it")
            // todo : 당장은 화면 진입 기능으로 쓰이지만 추후 위치 정보를 띄워주는 기능으로 변경
            onPhotoClick(it)
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