package com.example.myvpnclient.presentation

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

@Composable
fun MainScreen(viewModel: MainScreenViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = viewModel::onClickButtonDisConnect,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(50.dp),
        ) {
            Text(text = "Connect")
        }
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
            onClick = viewModel::onClickButtonConnect,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(50.dp),
            enabled = viewModel.isFormValid.value,
        ) {
            Text(text = "Connect")
        }
    }
}