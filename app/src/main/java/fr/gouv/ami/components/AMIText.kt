package fr.gouv.ami.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun Title(text: String, modifier: Modifier = Modifier) {
    Text(text, modifier, fontSize = 22.sp, fontWeight = FontWeight.Bold)
}