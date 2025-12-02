package fr.gouv.ami.dev.home

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.gouv.ami.api.baseUrl
import fr.gouv.ami.components.Tile
import fr.gouv.ami.data.models.Review
import fr.gouv.ami.data.repository.getReviewApps
import fr.gouv.ami.ui.theme.AMITheme
import kotlinx.coroutines.flow.catch

@Composable
fun ReviewAppsScreen(onSelectedReviewApp: () -> Unit) {

    var reviews by remember {
        mutableStateOf<MutableList<Review>?>(null)
    }

    LaunchedEffect(Unit) {
        val reviewFlow = getReviewApps()
        reviewFlow
            .catch { e ->
                Log.d("ReleasePickerScreen", e.toString())
            }
            .collect {
                val mainReview: Review =
                    Review(url = "https://ami-back-staging.osc-fr1.scalingo.io",
                        title = "Branche principale",
                        number = "0",
                        description = "Branche principale")
                    listOf("", "")
                reviews = it.body()
                reviews?.add(0, mainReview)
            }
    }

    return Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxWidth()
        ) {
            Text(
                modifier = Modifier.padding(16.dp),
                text = "Choix de la review",
                fontSize = 20.sp
            )
            LazyColumn {
                if (reviews != null) {
                    itemsIndexed(reviews!!) { _, review ->
                        Tile(
                            title = review.title,
                            content = review.description ?: ""
                        ) {
                            baseUrl = review.url
                            onSelectedReviewApp()
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewReleasePickerScreenLight() {
    AMITheme {
        ReviewAppsScreen() {}
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewReleasePickerScreenDark() {
    AMITheme {
        ReviewAppsScreen() {}
    }
}