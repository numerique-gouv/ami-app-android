package fr.gouv.ami.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import fr.gouv.ami.R

val MarianneFamily = FontFamily(
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
)

val Typography = Typography(
    /** Display **/
    displayLarge = TextStyle( // XL
        fontFamily = MarianneFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 72.sp
    ),
    displayMedium = TextStyle( // LG
        fontFamily = MarianneFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 64.sp
    ),
    displaySmall = TextStyle( // MD
        fontFamily = MarianneFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 56.sp
    ),

    /** Headline **/
    headlineLarge = TextStyle( // H1
        fontFamily = MarianneFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp
    ),
    headlineMedium = TextStyle( // H2
        fontFamily = MarianneFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp
    ),
    headlineSmall = TextStyle( // H3
        fontFamily = MarianneFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp
    ),

    /** Title **/
    titleLarge = TextStyle( // H4
        fontFamily = MarianneFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp
    ),
    titleMedium = TextStyle( // H5
        fontFamily = MarianneFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp
    ),
    titleSmall = TextStyle( // H6
        fontFamily = MarianneFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp
    ),

    /** Body **/
    bodyLarge = TextStyle( // XL
        fontFamily = MarianneFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp
    ),
    bodyMedium = TextStyle( // LG
        fontFamily = MarianneFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp
    ),
    bodySmall = TextStyle( // MD
        fontFamily = MarianneFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),

    /** Label **/
    labelLarge = TextStyle( // SM
        fontFamily = MarianneFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    labelSmall = TextStyle( // XS
        fontFamily = MarianneFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    )
)