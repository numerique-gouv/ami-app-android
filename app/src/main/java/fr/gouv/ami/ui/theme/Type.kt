package fr.gouv.ami.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import fr.gouv.ami.R

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily(
            listOf(
                Font(
                    R.font.marianne_regular,
                    weight = FontWeight.Normal,
                    style = FontStyle.Normal
                ),
                Font(
                    R.font.marianne_regular_italic,
                    weight = FontWeight.Normal,
                    style = FontStyle.Italic,
                ),
                Font(
                    R.font.marianne_bold,
                    weight = FontWeight.Bold
                ),
                Font(
                    R.font.marianne_bold_italic,
                    weight = FontWeight.Bold,
                    style = FontStyle.Italic
                )
            )
        ),
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)