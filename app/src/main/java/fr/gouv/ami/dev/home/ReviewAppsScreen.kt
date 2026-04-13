package fr.gouv.ami.dev.home

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.gouv.ami.R
import fr.gouv.ami.api.baseUrl
import fr.gouv.ami.components.Tile
import fr.gouv.ami.global.BaseScreen
import fr.gouv.ami.ui.theme.AMITheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewAppsScreen(onSelectedReviewApp: () -> Unit) {
    val TAG = "ReviewAppsScreen"

    val reviewAppsViewModel = viewModel<ReviewAppsViewModel>()

    val reviews by reviewAppsViewModel.reviews.collectAsState()
    val isRefreshing by reviewAppsViewModel.isRefreshing.collectAsState()

    LaunchedEffect(Unit) {
        reviewAppsViewModel.refreshData()
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
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = { reviewAppsViewModel.refreshData() },
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                LazyColumn {
                    if (reviews != null) {
                        itemsIndexed(reviews!!) { _, review ->
                            Tile(
                                title = review.title,
                                content = review.description ?: ""
                            ) {
                                baseUrl = review.url
                                Log.d(
                                    TAG,
                                    "Review app '${review.title}' selected with url: ${review.url}"
                                )
                                onSelectedReviewApp()
                            }
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
        ReviewAppsScreen {}
    }
}
