package fr.gouv.ami.components

import DsfrColors.background_action_high_info
import DsfrColors.background_contrast_info
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.gouv.ami.R
import fr.gouv.ami.ui.theme.AMITheme

@Composable
fun ServiceWithoutFollowup(
    onCloseButton: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(background_contrast_info)
            .padding(16.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painterResource(R.drawable.dsfr_info_fill),
                    contentDescription = "information",
                    colorFilter = ColorFilter.tint(background_action_high_info),
                    modifier = Modifier
                        .padding(0.dp)
                        .clip(CircleShape),
                )
                Text(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .weight(1f),
                    text = stringResource(R.string.service_without_followup_title),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                IconButton(onClick = onCloseButton) {
                    Image(
                        painterResource(R.drawable.ic_close),
                        contentDescription = "close",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Text(
                stringResource(R.string.service_without_followup_description),
                fontSize = 14.sp
            )
        }
    }
}

@Preview
@Composable
fun PreviewServiceWithoutFollowup() {
    AMITheme() {
        ServiceWithoutFollowup() {}
    }
}