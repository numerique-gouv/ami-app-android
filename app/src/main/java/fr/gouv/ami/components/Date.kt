package fr.gouv.ami.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import fr.gouv.ami.ui.theme.AMITheme

@Composable
fun Date(date: String, hasBackground: Boolean = true) {
    Card(
        shape = RoundedCornerShape(
            12.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (hasBackground)
                Color("#EEEEEE".toColorInt())
            else Color.Transparent,
            contentColor = if (hasBackground)
                Color.Black
            else
            Color("#666666".toColorInt())
        ),
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Text(
                text = date,
                fontSize = 12.sp
            )
        }
    }
}

@Preview
@Composable
fun PreviewDateBg() {
    AMITheme {
        Date("Du 5 au 22 avril")
    }
}

@Preview
@Composable
fun PreviewDate() {
    AMITheme {
        Date("le 9 juin à 14H34", hasBackground = false)
    }
}