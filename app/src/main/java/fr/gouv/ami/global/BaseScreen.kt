package fr.gouv.ami.global

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.gouv.ami.components.InformationBanner
import fr.gouv.ami.components.InformationType
import fr.gouv.ami.utils.NetworkMonitor
import fr.gouv.ami.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseScreen(
    hasTopBar: Boolean = false,
    topBarTitle: String = "",
    viewModel: BaseViewModel, content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val networkMonitor = remember { NetworkMonitor(context) }
    val isConnected by networkMonitor.isConnected.collectAsState(true)

    LaunchedEffect(isConnected) {
        if (isConnected) {
            viewModel.requestRefresh()
        }
    }

    Scaffold(
        topBar = {
            if (hasTopBar)
                TopAppBar(
                    title = {
                        Text(topBarTitle)
                    },
                    navigationIcon = {
                        IconButton(onClick = { }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                "backIcon"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                )
        },
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            if (!isConnected)
                InformationBanner(
                    informationType = InformationType.Warning,
                    title = stringResource(R.string.no_connection),
                    icon = R.drawable.ic_no_connection,
                    content = stringResource(R.string.no_connection_subtitle),
                    hasCloseIcon = false
                )

            content()
        }
    }
}
