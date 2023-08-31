package com.cesoft.cesbarcodetest.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cesoft.cesbarcodetest.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainCompo() {
    val permissionState = rememberPermissionState(android.Manifest.permission.CAMERA)
    if(permissionState.status.isGranted) {
        CameraCompo()
    }
    else {
        LaunchedEffect(permissionState) {
            permissionState.launchPermissionRequest()
        }
        NoPermissionErrorCompo(permissionState::launchPermissionRequest)
    }
}

@Composable
private fun NoPermissionErrorCompo(callback: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(50.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text= stringResource(R.string.permission_msg),
            modifier = Modifier.padding(20.dp),
            fontWeight = FontWeight.Bold,
        )
        Button(onClick = { callback() }) {
            Icon(imageVector = Icons.Default.Camera, "")
            Text(
                text = stringResource(R.string.permission_grant),
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 5.dp),
            )
        }
    }
}

//--------------------------------------------------------------------------------------------------
@Preview(showBackground = true)
@Composable
fun MainComposable_Error_Preview() {
    NoPermissionErrorCompo{}
}
