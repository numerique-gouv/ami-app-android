package fr.gouv.ami.global

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.gouv.ami.components.InformationBanner
import fr.gouv.ami.components.InformationType
import fr.gouv.ami.utils.NetworkMonitor
import fr.gouv.ami.R

@Composable
fun BaseScreen(viewModel: BaseViewModel, content: @Composable () -> Unit) {
    val context = LocalContext.current
    val networkMonitor = remember { NetworkMonitor(context) }
    val isConnected by networkMonitor.isConnected.collectAsState(true)

    LaunchedEffect(isConnected) {
        if (isConnected) {
            viewModel.requestRefresh()
        }
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            if (!isConnected)
                InformationBanner(
                    informationType = InformationType.Warning,
                    title = stringResource(R.string.no_connexion),
                    icon = R.drawable.ic_no_connexion,
                    content = stringResource(R.string.no_connextion_subtitle),
                    hasCloseIcon = false
                )

            content()
        }
    }
}