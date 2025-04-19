package ny.photomap.ui.mainmap

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.DialogProperties
import ny.photomap.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AskingPermissionDialog(
    isFirstUsage: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    BasicAlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnClickOutside = false)
    ) {
        Column(
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(dimensionResource(
                    R.dimen.margin_small)))
                .padding(
                    dimensionResource(R.dimen.margin_medium)
                )
        ) {
            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                textAlign = TextAlign.Center,
                text = if (isFirstUsage) stringResource(R.string.dialog_sync_first) else stringResource(
                    R.string.dialog_sync_request
                ),
                style = MaterialTheme.typography.bodyLarge
            )
            Row(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = dimensionResource(R.dimen.margin_medium))
            ) {
                FilledTonalButton(onClick = onConfirm) {
                    Text(stringResource(R.string.button_progress))
                }
                TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.button_reject))
                }
            }
        }

    }
}