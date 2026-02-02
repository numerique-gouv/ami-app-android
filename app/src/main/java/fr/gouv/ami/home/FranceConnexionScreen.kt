package fr.gouv.ami.home

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withLink
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.gouv.ami.R
import fr.gouv.ami.ui.theme.AMITheme

@Composable
fun FranceConnexionScreen(onFcClick: () -> Unit) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_application),
                    contentDescription = "image d'application"
                )
                Text(
                    modifier = Modifier.padding(vertical = 16.dp),
                    text = stringResource(R.string.franceConnexion_description), fontSize = 16.sp
                )
                Text(
                    modifier = Modifier.padding(
                        vertical = 24.dp
                    ),
                    text = stringResource(R.string.franceConnexion_subtitle),
                    fontSize = 12.sp,
                    color = Gray
                )
                Button(
                    onClick = onFcClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                ) {
                    Image(
                        painterResource(R.drawable.franceconnect_btn_principal),
                        contentDescription = "franceConnect button"
                    )
                }
                /*TextButton(
                    onClick =
                ) { }*/
                Text(
                    buildAnnotatedString {
                        withLink(
                            LinkAnnotation.Url(
                                "https://franceconnect.gouv.fr/",
                                //TextLinkStyles(style = SpanStyle(color = Color.Blue))
                            )
                        ) {
                            append(stringResource(R.string.FC_button_description))
                        }
                    },
                    modifier = Modifier.padding(12.dp)
                )
            }
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(stringResource(R.string.connection_difficult))
                Text(
                    buildAnnotatedString {
                        withLink(
                            LinkAnnotation.Url(
                                "",
                                //TextLinkStyles(style = SpanStyle(color = Color.Blue))
                            )
                        ) {
                            append(stringResource(R.string.tchap))
                        }
                    },
                    modifier = Modifier.padding(12.dp)
                )
            }

        }
    }
}


@Preview
@Composable
fun PreviewFranceConnexionScreenLight() {
    AMITheme {
        FranceConnexionScreen(){}
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewFranceConnexionScreenDark() {
    AMITheme {
        FranceConnexionScreen(){}
    }
}