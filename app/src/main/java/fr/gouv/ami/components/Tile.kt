package fr.gouv.ami.components

import android.content.res.Configuration
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.gouv.ami.ui.theme.AMITheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Tile(title: String, content: String, action: () -> Unit) {
    val color = MaterialTheme.colorScheme.primary
    return Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .drawBehind {
                val strokeWidth = 8.dp.toPx()
                val y = size.height

                drawLine(
                    color = color,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = strokeWidth
                )
            }
            .border(1.dp, Color.Gray, RectangleShape),
        onClick = action
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(title, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
            Text(content)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Icon(
                    Icons.AutoMirrored.Outlined.ArrowForward,
                    "arrow right",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewTileLight() {
    AMITheme {
        Tile(
            title = "PR239",
            content = "build two apps per platforms"
        ) {}
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewTileDark() {
    AMITheme {
        Tile(
            title = "PR239",
            content = "build two apps per platforms"
        ) {}
    }
}