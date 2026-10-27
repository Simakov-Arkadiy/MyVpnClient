package com.example.myvpnclient.presentation

import android.app.Activity
import android.net.VpnService
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext

@Composable
fun MainScreen(viewModel: MainScreenViewModel) {
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.onClickButtonConnect()
        } else {
            Toast.makeText(context, "VPN permission denied", Toast.LENGTH_SHORT).show()
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = viewModel.valueForAuthCodeTextField.value,
            onValueChange = viewModel::onAuthCodeSymbolChanged,
            label = { Text("AuthCode") })

        TextField(
            value = viewModel.valueForServerHostTextField.value,
            onValueChange = viewModel::onServerHostSymbolChanged,
            label = { Text("ServerHost") })
        TextField(
            value = viewModel.valueForServerPortTextField.value,
            onValueChange = viewModel::onServerPortSymbolChanged,
            label = { Text("ServerPort") })
        Button(
            onClick = {
                val intent = VpnService.prepare(context)
                if (intent != null) permissionLauncher.launch(intent) else viewModel.onClickButtonConnect()
            },
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(50.dp),
            enabled = viewModel.isFormValid.value,
        ) {
            Text(text = "Connect")
        }
        Button(
            onClick = viewModel::onClickButtonDisConnect,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(50.dp),
        ) {
            Text(text = "Disconnect")
        }
    }
}