package ny.photomap

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import ny.photomap.ui.theme.PhotoMapTheme



@Composable
fun SuccessFailureSnackbar(
    data: SnackbarData,
    modifier: Modifier = Modifier,
) {

    val isSuccessNotice =
        if (data.visuals.message.contains(stringResource(R.string.success))) true
        else if (data.visuals.message.contains(stringResource(R.string.failure))) false
        else null

    val (contentColor, containerColor, actionColor, actionContentColor) = when (isSuccessNotice) {
        true -> listOf<Color>(
            MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.tertiary, MaterialTheme.colorScheme.primaryContainer
        )

        false -> listOf<Color>(
            MaterialTheme.colorScheme.error, MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.tertiary, MaterialTheme.colorScheme.errorContainer
        )

        else -> listOf<Color>(
            MaterialTheme.colorScheme.tertiary, MaterialTheme.colorScheme.surfaceContainer,
            MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.surfaceContainer
        )
    }
    Snackbar(
        modifier = modifier,
        snackbarData = data,
        containerColor = containerColor,
        contentColor = contentColor,
        actionColor = actionColor,
        actionContentColor = actionContentColor,
    )
}

@Preview
@Composable
fun SuccessSnackbarPreview() {
    val host = remember {
        SnackbarHostState()
    }
    PhotoMapTheme {
        Scaffold(snackbarHost = {
            SnackbarHost(host) {
                SuccessFailureSnackbar(
                    it
                )
            }
        }) { it ->
            Box(modifier = Modifier.padding(it))
        }
    }

    LaunchedEffect(Unit) {
        host.showSnackbar(
            message = "성공 메시지를 보고 싶다.",
            actionLabel = "가자",
            duration = SnackbarDuration.Indefinite
        )
    }
}

@Preview
@Composable
fun FailureSnackbarPreview() {
    val host = remember {
        SnackbarHostState()
    }
    PhotoMapTheme {
        Scaffold(snackbarHost = {
            SnackbarHost(host) {
                SuccessFailureSnackbar(
                    it
                )
            }
        }) { it ->
            Box(modifier = Modifier.padding(it))
        }
    }

    LaunchedEffect(Unit) {
        host.showSnackbar(
            message = "실패 메시지를 보고 싶다.",
            actionLabel = "가자",
            duration = SnackbarDuration.Indefinite
        )
    }
}

@Preview
@Composable
fun DefaultSnackbarPreview() {
    val host = remember {
        SnackbarHostState()
    }
    PhotoMapTheme {
        Scaffold(snackbarHost = {
            SnackbarHost(host) {
                SuccessFailureSnackbar(
                    it
                )
            }
        }) { it ->
            Box(modifier = Modifier.padding(it))
        }
    }

    LaunchedEffect(Unit) {
        host.showSnackbar(
            message = "일반 메시지를 보고 싶다.",
            actionLabel = "가자",
            duration = SnackbarDuration.Indefinite
        )
    }
}