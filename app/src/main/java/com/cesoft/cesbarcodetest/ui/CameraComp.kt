package com.cesoft.cesbarcodetest.ui

import android.content.Context
import android.graphics.Color
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraCompo() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraController = remember { LifecycleCameraController(context) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {}
    ) { paddingValues: PaddingValues ->
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            factory = { context ->
                PreviewView(context).apply {
                    layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                    setBackgroundColor(Color.BLACK)
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                    scaleType = PreviewView.ScaleType.FILL_START
                }.also { previewView ->
                    previewView.controller = cameraController
                    cameraController.bindToLifecycle(lifecycleOwner)
                    startCamera(context, previewView, lifecycleOwner, cameraController)
                }
            }
        )
    }
}

private fun startCamera(
    context: Context,
    previewView: PreviewView,
    lifecycleOwner: LifecycleOwner,
    cameraController: LifecycleCameraController,
) {
    val imageAnalysisAnalyzer = getBarcodeAnalyzer(context) { barcodes ->
        previewView.overlay.clear()
        for(barcode in barcodes) {
            val drawable = BarcodeDrawable(barcode)
            previewView.overlay.add(drawable)
        }
    }
    cameraController.setImageAnalysisAnalyzer(
        ContextCompat.getMainExecutor(context),
        imageAnalysisAnalyzer
    )

    cameraController.bindToLifecycle(lifecycleOwner)
    previewView.controller = cameraController
}

private fun getBarcodeAnalyzer(
    context: Context,
    callback: (List<BarcodeData>) -> Unit
): MlKitAnalyzer {
    val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
        .build()
    val barcodeScanner = BarcodeScanning.getClient(options)
    return MlKitAnalyzer(
        listOf(barcodeScanner),
        CameraController.COORDINATE_SYSTEM_VIEW_REFERENCED,
        ContextCompat.getMainExecutor(context)
    ) { result: MlKitAnalyzer.Result? ->
        val barcodeResults = result?.getValue(barcodeScanner)
        val res = barcodeResults?.map {
            val error = it.rawValue!!.startsWith("84")
            BarcodeData(it, error)
        } ?: listOf()
        callback(res)
    }
}