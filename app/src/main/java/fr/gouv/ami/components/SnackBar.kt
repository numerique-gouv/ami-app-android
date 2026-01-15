package fr.gouv.ami.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.gouv.ami.R
import fr.gouv.ami.ui.theme.AMITheme
import fr.gouv.ami.ui.theme.GrayNeutral
import fr.gouv.ami.ui.theme.GreenInformationContent
import fr.gouv.ami.ui.theme.RedInformationContent

enum class SnackBarType {
    Success,
    Error,
    Neutral,
}

@Composable
fun SnackBar(type: SnackBarType, text: String, onClick: () -> Unit) {
    val color = when (type) {
        SnackBarType.Success -> GreenInformationContent
        SnackBarType.Error -> RedInformationContent
        SnackBarType.Neutral -> GrayNeutral
    }

    Surface(
        color = color,
        contentColor = Color.White,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (type != SnackBarType.Neutral)
                Image(
                    painter = painterResource(
                        if (type == SnackBarType.Success)
                            R.drawable.fr__success
                        else
                            R.drawable.close_circle
                    ),
                    contentDescription = "icone",
                    colorFilter = ColorFilter.tint(Color.White)
                )
            Text(
                text, modifier = Modifier
                    .padding(8.dp)
                    .weight(1f),
                fontSize = 14.sp
            )
            if (type != SnackBarType.Neutral)
                IconButton(onClick = onClick) {
                    Image(
                        painter = painterResource(R.drawable.close_line),
                        contentDescription = "Fermer",
                        colorFilter = ColorFilter.tint(Color.White)
                    )
                }
            else
                TextButton(onClick = onClick) {
                    Text(
                        "Annuler",
                        color = Color.White,
                        fontSize = 14.sp,
                        style = TextStyle(textDecoration = TextDecoration.Underline),
                    )
                }
        }
    }
}

@Preview
@Composable
fun PreviewSnackBarSuccess() {
    AMITheme {
        SnackBar(
            type = SnackBarType.Success,
            "Maecenas rhoncus, magna vel sollicitudin vehicula, mi nunc venenatis ex."
        ){}
    }
}

@Preview
@Composable
fun PreviewSnackBarError() {
    AMITheme {
        SnackBar(
            type = SnackBarType.Error,
            text = "Maecenas rhoncus, magna vel sollicitudin vehicula, mi nunc venenatis ex."
        ){}
    }
}

@Preview
@Composable
fun PreviewSnackBarNeutral() {
    AMITheme {
        SnackBar(
            type = SnackBarType.Neutral,
            text = "Maecenas rhoncus, magna vel sollicitudin vehicula, mi nunc venenatis ex."
        ){}
    }
}