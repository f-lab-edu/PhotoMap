package ny.photomap.permission

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ny.photomap.ui.theme.PhotoMapTheme
import ny.photomap.ui.theme.Typography

@Composable
fun PermissionRequestNotice(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    requestButtonText: String,
    onClickRequestButton: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.surface)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(weight = 1f, fill = false)) {
            Text(modifier = Modifier.fillMaxWidth(), text = title, style = Typography.titleLarge)
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = description,
                style = Typography.bodyMedium
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Button(onClick = onClickRequestButton) {
            Text(text = requestButtonText)
        }
    }


}

@Preview
@Composable
fun PermissionRequestNoticeTest() {
    PhotoMapTheme {
        PermissionRequestNotice(
            title = "파일 읽기 권한",
            description = "권한이 필요합니다. \n 줄이 길어지면 어떻게 보이는 지 궁금합니다. 길게 쓰겠습니다.",
            requestButtonText = "권한 승인",
        ) {

        }
    }
}