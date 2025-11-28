package fr.gouv.ami.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.gouv.ami.ui.theme.AMITheme
import fr.gouv.ami.ui.theme.BlueFranceMain
import java.net.URL

@Composable
fun Tile(title: String, url: URL, content: String) {
    return Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .drawBehind {
                val strokeWidth = 8.dp.toPx()
                val y = size.height

                drawLine(
                    color = BlueFranceMain,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = strokeWidth
                )
            }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(title, fontSize = 18.sp)
            Text(content)
        }
    }
}

@Preview
@Composable
fun PreviewTileLight() {
    AMITheme {
        Tile(
            title = "PR239",
            url = URL("https://google.com"),
            content = "build two apps per platforms"
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewTileDark() {
    AMITheme {
        Tile(
            title = "PR239",
            url = URL("https://google.com"),
            content = "build two apps per platforms"
        )
    }
}