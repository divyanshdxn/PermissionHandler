package com.dxn.permissionhandler

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.dxn.permissionhandler.ui.theme.PermissionHandlerTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberMultiplePermissionsState

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val permissionState = rememberMultiplePermissionsState(
                permissions = listOf(
                    android.Manifest.permission.RECORD_AUDIO,
                    android.Manifest.permission.CAMERA
                )
            )
            PermissionHandlerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    Column(
                        Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        val lifecycleOwner = LocalLifecycleOwner.current
                        DisposableEffect(key1 = lifecycleOwner) {
                            val observer = LifecycleEventObserver { _, event ->
                                if (event == Lifecycle.Event.ON_START) {
                                    permissionState.launchMultiplePermissionRequest()
                                }
                            }
                            lifecycleOwner.lifecycle.addObserver(observer)

                            onDispose {
                                lifecycleOwner.lifecycle.removeObserver(observer)
                            }
                        }

                        permissionState.permissions.forEach { perm ->
                            when (perm.permission) {
                                android.Manifest.permission.CAMERA -> {
                                    when (val sts = perm.status) {

                                        is PermissionStatus.Denied -> {
                                            if (sts.shouldShowRationale) {
                                                // denied once
                                                Text(text = "Camera permissions denied, tap to allow")
                                                Button(onClick = { permissionState.launchMultiplePermissionRequest() }) {
                                                    Text(text = "Allow camera permission")
                                                }
                                            } else {
                                                // permanently denied
                                                Text(text = "Camera permissions permanently denied, you need to go to settings and allow")

                                            }
                                        }
                                        PermissionStatus.Granted -> {
                                            Text(text = "Camera permissions accepted")
                                        }
                                    }
                                }

                                android.Manifest.permission.RECORD_AUDIO -> {
                                    when (val sts = perm.status) {

                                        is PermissionStatus.Denied -> {
                                            if (sts.shouldShowRationale) {
                                                // denied once
                                                Text(text = "Microphone permissions denied, tap to allow")
                                            } else {
                                                // permanently denied
                                                Text(text = "Microphone permissions permanently denied, you need to go to settings and allow")

                                            }
                                        }
                                        PermissionStatus.Granted -> {
                                            Text(text = "Microphone permissions accepted")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
