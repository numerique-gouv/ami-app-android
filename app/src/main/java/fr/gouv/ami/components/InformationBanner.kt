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

enum class InformationType {
    Warning,
    Information,
    Error,
    Validation,
}

@Composable
fun InformationBanner(
    informationType: InformationType,
    title: String,
    content: String? = null,
    link: String? = null,
    hasCloseIcon: Boolean = true,
    onCLickLink: () -> Unit = {},
    onClose: () -> Unit = {}
) {
    var colorBg: Color
    var colorContent: Color
    var icon: Int

    when (informationType) {
        InformationType.Information -> {
            colorBg = BlueInformationBg
            colorContent = BlueInformationContent
            icon = R.drawable.ic_information_information
        }

        InformationType.Warning -> {
            colorBg = OrangeInformationBg
            colorContent = OrangeInformationContent
            icon = R.drawable.ic_information_warning
        }

        InformationType.Error -> {
            colorBg = RedInformationBg
            colorContent = RedInformationContent
            icon = R.drawable.ic_information_error
        }

        InformationType.Validation -> {
            colorBg = GreenInformationBg
            colorContent = GreenInformationContent
            icon = R.drawable.ic_information_validation
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
            Row (verticalAlignment = Alignment.CenterVertically) {
                Image(painterResource(icon), contentDescription = "banner icon")
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
            informationType = InformationType.Warning,
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
            informationType = InformationType.Information,
            title = "Nouvelle démarche disponible",
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
            informationType = InformationType.Error,
            title = "Application hors-service",
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
            informationType = InformationType.Validation,
            title = "Connexion rétablie",
            content = "L’application est de nouveau fonctionnelle.",
            link = "Lien de consultation",
        )
    }
}