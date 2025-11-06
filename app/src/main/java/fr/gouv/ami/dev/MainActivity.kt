package fr.gouv.ami.dev

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import fr.gouv.ami.dev.ui.theme.AMITheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val isNotification = intent.getBooleanExtra("notification", false)
        setContent {
            AMITheme {
                HomeApp(isNotification)
            }
        }
    }
}