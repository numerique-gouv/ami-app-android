package fr.gouv.ami.networkManager

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.gouv.ami.R
import fr.gouv.ami.ui.theme.AMITheme

@Composable
fun WifiErrorScreen() {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier.padding(8.dp),
                painter = painterResource(id = R.drawable.connection_lost),
                contentDescription = "connection lost"
            )
            Text(
                modifier = Modifier.padding(vertical = 8.dp),
                textAlign = TextAlign.Center,
                text = stringResource(R.string.connection_lost_title),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Text(
                //modifier = Modifier.padding(vertical = 8.dp),
                textAlign = TextAlign.Center,
                text = stringResource(R.string.connection_lost_description),
                fontSize = 16.sp
            )
        }
    }
}

@Preview
@Composable
fun WifiErrorScreenLight() {
    AMITheme {
        WifiErrorScreen()
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun WifiErrorScreenDark() {
    AMITheme {
        WifiErrorScreen()
    }
}