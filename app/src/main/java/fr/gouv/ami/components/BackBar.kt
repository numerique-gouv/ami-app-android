package fr.gouv.ami.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import fr.gouv.ami.R
import fr.gouv.ami.ui.theme.AMITheme

@Composable
fun BackBar(action: () -> Unit) {
    return Surface(
        modifier = Modifier.fillMaxWidth(),
        /*contentColor = MaterialTheme.colorScheme.onPrimary,
        color = MaterialTheme.colorScheme.primary*/
    ) {
        Row(modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = action,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.onPrimary)) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "back",
                )
            }
            Text(stringResource(R.string.back))
        }
    }
}

@Preview
@Composable
fun PreviewBackBarLight() {
    AMITheme {
        BackBar(){}
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewBackBarDark() {
    AMITheme {
        BackBar(){}
    }
}