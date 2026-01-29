package fr.gouv.ami.dev.home

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.gouv.ami.R
import fr.gouv.ami.api.baseUrl
import fr.gouv.ami.components.Tile
import fr.gouv.ami.data.models.Review
import fr.gouv.ami.data.repository.getReviewApps
import fr.gouv.ami.global.BaseScreen
import fr.gouv.ami.ui.theme.AMITheme
import kotlinx.coroutines.flow.catch

@Composable
fun ReviewAppsScreen(onSelectedReviewApp: () -> Unit) {
    val TAG = "ReviewAppsScreen"

    var reviews by remember {
        mutableStateOf<MutableList<Review>?>(null)
    }

    LaunchedEffect(Unit) {
        val reviewFlow = getReviewApps()
        reviewFlow
            .catch { e ->
                Log.e(TAG, "Error fetching review apps", e)
            }
            .collect { response ->
                if (response.isSuccessful) {
                    reviews = response.body()
                    Log.d(TAG, "Successfully loaded ${reviews?.size ?: 0} review apps")
                } else {
                    Log.e(
                        TAG,
                        "Error loading review apps: ${response.code()} - ${response.message()}"
                    )
                }
            }
    }

    return BaseScreen(viewModel = viewModel()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                modifier = Modifier.padding(16.dp),
                text = stringResource(R.string.reviewApp_title),
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
