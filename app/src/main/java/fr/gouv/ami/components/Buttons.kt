package fr.gouv.ami.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.gouv.ami.ui.theme.AMITheme
import fr.gouv.ami.ui.theme.BlueFranceSun113

@Composable
fun PrimaryButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = BlueFranceSun113,
            contentColor = Color.White
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(0.dp),
    ) {
        Text(text, fontSize = 18.sp)
    }
}

@Composable
fun SecondaryButton(text: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = BlueFranceSun113
        ),
        border = BorderStroke(1.dp, BlueFranceSun113),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(0.dp)
    ) {
        Text(text, fontSize = 18.sp)
    }
}

@Preview
@Composable
fun PreviewPrimaryButton() {
    AMITheme {
        PrimaryButton("Continuer") {}
    }
}

@Preview
@Composable
fun PreviewSecondaryButton() {
    AMITheme {
        SecondaryButton("Fermer") {}
    }
}