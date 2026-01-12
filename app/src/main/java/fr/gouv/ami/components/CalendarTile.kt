package fr.gouv.ami.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.gouv.ami.R
import fr.gouv.ami.data.models.Tag
import fr.gouv.ami.ui.theme.AMITheme
import fr.gouv.ami.ui.theme.GreenInformationBg

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Tile(
    title: String? = null,
    content: String? = null,
    contentSize: TextUnit = 14.sp,
    date: String? = null,
    hasDateBackground: Boolean = true,
    containerColor: Color = Color.White,
    tag: Tag? = null,
    hasManyAction: Boolean = false,
    icon: Int? = null,
    action: () -> Unit
) {
    val color = MaterialTheme.colorScheme.primary
    return Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .drawBehind {
                val strokeWidth = 8.dp.toPx()
                val y = size.height
                val x = size.width

                //bottom
                drawLine(
                    color = color,
                    start = Offset(-3f, y),
                    end = Offset(x + 3f, y),
                    strokeWidth = strokeWidth
                )

                //top
                drawLine(
                    color = Color.Gray,
                    start = Offset(-3f, 0f),
                    end = Offset(x + 3f, 0f),
                    strokeWidth = 2.dp.toPx()
                )

                //right
                drawLine(
                    color = Color.Gray,
                    start = Offset(x, 0f),
                    end = Offset(x, y),
                    strokeWidth = 2.dp.toPx()
                )

                //left
                drawLine(
                    color = Color.Gray,
                    start = Offset(0f, 0f),
                    end = Offset(0f, y),
                    strokeWidth = 2.dp.toPx()
                )
            },
        //.border(1.dp, Color.Gray, RectangleShape),
        onClick = action,
        color = containerColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .padding(16.dp)
        ) {
            if (icon != null)
                Column(verticalArrangement =
                    Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxHeight()
                        .padding(end = 4.dp)) {
                    Image(
                        painterResource(icon),
                        modifier = Modifier
                            .padding(end = 4.dp),
                        contentDescription = "icone du tag",
                    )
                }
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                if (tag != null)
                    TagComponent(
                        icon = tag.icon,
                        text = tag.text,
                        contentColor = tag.contentColor,
                        containerColor = tag.containerColor
                    )
                if (date != null) {
                    Date(date, hasBackground = hasDateBackground)
                }
                if (title != null)
                    Text(
                        text = title,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                if (content != null)
                    Text(text = content, fontSize = contentSize)

            }
            Column(
                verticalArrangement = if (hasManyAction)
                    Arrangement.SpaceBetween
                else
                    Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxHeight()
            ) {
                if (hasManyAction)
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = "plus d'actions",
                        tint = MaterialTheme.colorScheme.primary
                    )
                Icon(
                    Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                    "arrow right",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewTileCalendar() {
    AMITheme {
        Tile(
            title = "Opération Tranquillité Vacances \uD83C\uDFE0",
            content = "Inscrivez-vous pour protéger votre domicile pendant votre absence",
            date = "Du 5 au 22 avril",
            hasDateBackground = true,
            tag = Tag(
                icon = R.drawable.ic_home,
                text = "impot et taxe",
                contentColor = "#006A6F",
                containerColor = "#E5FBFD"
            ),
            hasManyAction = true
        ) {}
    }
}

@Preview
@Composable
fun PreviewTileDemarche() {
    AMITheme {
        Tile(
            content = "Informer son employeur en lui adressant une lettre de démission",
            hasManyAction = false,
            containerColor = GreenInformationBg,
            contentSize = 16.sp,
            icon = R.drawable.ic_information_validation
        ) {}
    }
}

@Preview
@Composable
fun PreviewTileAction() {
    AMITheme {
        Tile(
            title = "Titre de la catégorie",
            hasManyAction = false,
            icon = R.drawable.house
        ) {}
    }
}

/*@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewTileDark() {
    AMITheme {
        Tile(
            title = "PR239",
            content = "build two apps per platforms"
        ) {}
    }
}*/