package fr.gouv.ami.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.gouv.ami.R
import fr.gouv.ami.ui.theme.AMITheme
import fr.gouv.ami.ui.theme.BlueInformationBg
import fr.gouv.ami.ui.theme.BlueInformationContent
import fr.gouv.ami.ui.theme.GreenInformationBg
import fr.gouv.ami.ui.theme.GreenInformationContent
import fr.gouv.ami.ui.theme.OrangeInformationBg
import fr.gouv.ami.ui.theme.OrangeInformationContent
import fr.gouv.ami.ui.theme.RedInformationBg
import fr.gouv.ami.ui.theme.RedInformationContent

@Composable
fun InformationBanner(
    informationType: StatusType,
    title: String,
    icon: Int,
    content: String? = null,
    link: String? = null,
    hasCloseIcon: Boolean = true,
    onCLickLink: () -> Unit = {},
    onClose: () -> Unit = {}
) {
    var colorBg: Color
    var colorContent: Color

    when (informationType) {
        StatusType.Information -> {
            colorBg = BlueInformationBg
            colorContent = BlueInformationContent
        }

        StatusType.Warning -> {
            colorBg = OrangeInformationBg
            colorContent = OrangeInformationContent
        }

        StatusType.Error -> {
            colorBg = RedInformationBg
            colorContent = RedInformationContent
        }

        StatusType.Success -> {
            colorBg = GreenInformationBg
            colorContent = GreenInformationContent
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorBg)
            .padding(16.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painterResource(icon),
                    contentDescription = "banner icon",
                    colorFilter = ColorFilter.tint(colorContent)
                )
                Text(
                    modifier = Modifier.padding(start = 8.dp),
                    text = title,
                    color = colorContent,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            if (!content.isNullOrEmpty())
                Text(content, color = colorContent, fontSize = 14.sp)
            if (!link.isNullOrEmpty())
                Text(
                    link,
                    color = colorContent,
                    fontSize = 14.sp,
                    style = TextStyle(textDecoration = TextDecoration.Underline),
                    modifier = Modifier.clickable {
                        onCLickLink()
                    }
                )
        }

        if (hasCloseIcon) {
            IconButton(
                onClick = onClose,
                modifier = Modifier.size(32.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_close),
                    contentDescription = "close",
                    colorFilter = ColorFilter.tint(colorContent)
                )
            }
        }
    }
}

@Preview()
@Composable
fun PreviewInformationBannerWarning() {
    AMITheme {
        InformationBanner(
            informationType = StatusType.Warning,
            icon = R.drawable.ic_information_warning,
            title = "Connexion indisponible",
            content = "Vérifiez votre connexion et réessayez.",
            link = "Lien de consultation",
        )
    }
}

@Preview()
@Composable
fun PreviewInformationBannerInformation() {
    AMITheme {
        InformationBanner(
            informationType = StatusType.Information,
            title = "Nouvelle démarche disponible",
            icon = R.drawable.ic_information_information,
            content = "Vérifiez votre connexion et réessayez.",
            link = "Lien de consultation",
        )
    }
}

@Preview()
@Composable
fun PreviewInformationBannerError() {
    AMITheme {
        InformationBanner(
            informationType = StatusType.Error,
            title = "Application hors-service",
            icon = R.drawable.ic_information_error,
            content = "Vérifiez votre connexion et réessayez.",
            link = "Lien de consultation",
        )
    }
}

@Preview()
@Composable
fun PreviewInformationBannerValidation() {
    AMITheme {
        InformationBanner(
            informationType = StatusType.Success,
            title = "Connexion rétablie",
            icon = R.drawable.ic_information_validation,
            content = "L’application est de nouveau fonctionnelle.",
            link = "Lien de consultation",
        )
    }
}