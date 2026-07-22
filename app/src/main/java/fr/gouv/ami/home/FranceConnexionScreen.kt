package fr.gouv.ami.home

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.gouv.ami.R
import fr.gouv.ami.global.BaseScreen
import fr.gouv.ami.ui.theme.AMITheme
import fr.gouv.ami.utils.EmailManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FranceConnexionScreen(onFcClick: () -> Unit) {
    val targetTchap = "fr.gouv.tchap.a"
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current

    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    BaseScreen(viewModel = viewModel()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_application),
                    contentDescription = "image d'application"
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    text = stringResource(R.string.home_title)
                )
                Text(
                    modifier = Modifier.padding(
                        bottom = 24.dp
                    ),
                    text = stringResource(R.string.franceConnexion_subtitle),
                    fontSize = 14.sp
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
                TextButton(
                    onClick = { uriHandler.openUri("https://franceconnect.gouv.fr/") }
                ) {
                    Text(
                        fontSize = 14.sp,
                        textDecoration = TextDecoration.Underline,
                        text = stringResource(R.string.FC_button_description)
                    )
                }
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                TextButton(
                    onClick = {
                        showBottomSheet = true
                        //context.openOrInstallApp(targetTchap)
                    }
                ) {
                    Text(
                        fontSize = 16.sp,
                        textDecoration = TextDecoration.Underline,
                        text = stringResource(R.string.cannot_connect)
                    )
                }
            }
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    //ask for help online
                    Row(
                        modifier = Modifier
                            .padding(vertical = 12.dp)
                            .clickable(
                                onClick = {
                                    showBottomSheet = false
                                }),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            modifier = Modifier.padding(end = 8.dp),
                            painter = painterResource(R.drawable.dsfr_edit_fill),
                            contentDescription = "online",
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                        )
                        Text(stringResource(R.string.help_online))
                    }
                    //send an email
                    Row(
                        modifier = Modifier
                            .padding(vertical = 12.dp)
                            .clickable(onClick = {
                                showBottomSheet = false
                                val emailManager = EmailManager(context)
                                emailManager.emailTo()
                            }),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            modifier = Modifier.padding(end = 8.dp),
                            painter = painterResource(R.drawable.dsfr_mail_fill),
                            contentDescription = "email",
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                        )
                        Text(stringResource(R.string.help_email))
                    }
                }
            }
        }
    }
}

fun Context.isAppInstalled(packageName: String): Boolean {
    return try {
        packageManager.getPackageInfo(packageName, 0)
        true
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }
}

fun Context.openOrInstallApp(packageName: String) {
    if (isAppInstalled(packageName)) {
        val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
        launchIntent?.let { startActivity(it) }
    } else {
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("market://details?id=$packageName")
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}


@Preview
@Composable
fun PreviewFranceConnexionScreenLight() {
    AMITheme {
        FranceConnexionScreen() {}
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewFranceConnexionScreenDark() {
    AMITheme {
        FranceConnexionScreen() {}
    }
}