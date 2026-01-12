package fr.gouv.ami.components

import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.gouv.ami.R
import fr.gouv.ami.ui.theme.AMITheme
import androidx.core.graphics.toColorInt
import fr.gouv.ami.data.models.Tag

@Composable
fun TagComponent(
    icon: Int,
    text: String,
    contentColor: String,
    containerColor: String
) {
    val upperCaseText = text.uppercase()

    Card(
        shape = RoundedCornerShape(
            4.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color(containerColor.toColorInt()),
            contentColor = Color(contentColor.toColorInt())
        ),
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
        ) {
            Image(
                painterResource(icon),
                modifier = Modifier
                    .padding(end = 4.dp),
                contentDescription = "icone du tag",
                colorFilter = ColorFilter.tint(Color(contentColor.toColorInt()))
            )
            Text(
                text = upperCaseText,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview
@Composable
fun PreviewTagBlue() {
    AMITheme {
        TagComponent(
            icon = R.drawable.ic_information_validation,
            text = "impot et taxe",
            contentColor = "#006A6F",
            containerColor = "#E5FBFD"
        )
    }
}