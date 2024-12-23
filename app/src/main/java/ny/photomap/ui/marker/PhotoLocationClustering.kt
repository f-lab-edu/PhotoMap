package ny.photomap.ui.marker

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import com.google.maps.android.compose.GoogleMapComposable
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.clustering.Clustering
import ny.photomap.R
import ny.photomap.model.PhotoLocationUIModel
import timber.log.Timber

@OptIn(MapsComposeExperimentalApi::class)
@GoogleMapComposable
@Composable
fun PhotoLocationClustering(items: List<PhotoLocationUIModel>) {

    Clustering(
        items = items,
        // Optional: Handle clicks on clusters, cluster items, and cluster item info windows
        onClusterClick = {
            Timber.d("Cluster clicked! $it")
            false
        },
        onClusterItemClick = {
            Timber.d("Cluster item clicked! $it")
            false
        },
        onClusterItemInfoWindowClick = {
            Timber.d("Cluster item info window clicked! $it")
        },
        clusterContent = { cluster ->
            PhotoLocationMarker(
                modifier = Modifier.size(dimensionResource(R.dimen.size_thumbnail)),
                text = "${cluster.size}",
                color = Color.Green,
                model = cluster.items.last()
            )
        },
        clusterItemContent = { item ->
            PhotoLocationMarker(
                modifier = Modifier.size(dimensionResource(R.dimen.size_thumbnail)),
                text = "",
                color = Color.Green,
                model = item
            )
        },
    )
}