package fr.gouv.ami.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.gouv.ami.R
import fr.gouv.ami.ui.theme.AMITheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportFileBottomSheet(
    sheetState: SheetState,
    onDismissRequest: () -> Unit,
    onFileSelected: () -> Unit,
    onCameraSelected: () -> Unit
) {

    ModalBottomSheet(
        onDismissRequest = {
            onDismissRequest()
        },
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.dsfr_file_download_line),
                    contentDescription = "Sélectionner un fichier"
                )
                Text(
                    modifier = Modifier
                        .clickable { onFileSelected() }
                        .padding(start = 16.dp),
                    text = stringResource(R.string.download_menu_file)
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.dsfr_camera_fill),
                    contentDescription = "Prendre une photo"
                )
                Text(
                    modifier = Modifier
                        .clickable { onCameraSelected() }
                        .padding(start = 16.dp),
                    text = stringResource(R.string.download_menu_camera))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun PreviewImportFileBottomSheet() {
    AMITheme() {
        ImportFileBottomSheet(
            sheetState = rememberModalBottomSheetState(),
            onDismissRequest = {},
            onFileSelected = {},
            onCameraSelected = {})
    }
}