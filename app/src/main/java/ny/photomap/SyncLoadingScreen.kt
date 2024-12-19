package ny.photomap

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.delay
import ny.photomap.ui.theme.PhotoMapTheme

@Composable
fun SyncLoadingScreen() {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
    ) { padding ->
        val loadingTexts = stringArrayResource(R.array.loading_sync)

        var index by remember {
            mutableIntStateOf(0)
        }

        LaunchedEffect(Unit) {
            val lastIndex = loadingTexts.lastIndex
            while (true) {
                delay(4000)
                if (index + 1 > lastIndex) {
                    index = 0
                } else index++
            }
        }
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            Spacer(modifier = Modifier.weight(1f))
            LinearProgressIndicator(
                modifier = Modifier
                    .padding(dimensionResource(R.dimen.margin_large))
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.primaryContainer,
            )
            Text(
                text = loadingTexts[index],
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .align(
                        Alignment.CenterHorizontally
                    )
                    .weight(1f, fill = false)
                    .padding(dimensionResource(R.dimen.margin_large)),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.weight(1f))
        }

    }
}

@Preview
@Composable
fun SyncLoadingScreenPreview() {
    PhotoMapTheme {
        SyncLoadingScreen()
    }
}