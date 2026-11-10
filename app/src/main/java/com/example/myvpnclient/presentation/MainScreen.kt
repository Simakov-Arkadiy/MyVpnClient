package com.example.myvpnclient.presentation

import android.app.Activity
import android.net.VpnService
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity

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
    var isDropdownVisible by remember { mutableStateOf(false) }
    var anchorWidth by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box( modifier = Modifier
            .fillMaxWidth(0.8f)
            .onGloballyPositioned { coords ->
                anchorWidth = with(density) {
                    coords.size.width.toDp()
                }
            },
            contentAlignment = Alignment.Center) {
            Button(
                onClick = { isDropdownVisible = !isDropdownVisible },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
            ) {
                Text(text = "in Tunnel")
            }
            DropdownMenu(
                expanded = isDropdownVisible,
                { isDropdownVisible = false },
                modifier = Modifier.width(anchorWidth)
            ) {
                viewModel.valueForDropDown.value.forEachIndexed { index, info ->
                    DropdownMenuItem(
                        onClick = { viewModel.onCheckedChange(index, info.packageName) },
                        text = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Image(
                                    bitmap = info.icon,
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp),
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Text(text = info.label)
                                Spacer(modifier = Modifier.weight(1f))
                                Checkbox(onCheckedChange = null, checked = viewModel.isCheckBoxActive[index])
                            }
                        }
                    )
                }
            }
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
            onClick = {
                val intent = VpnService.prepare(context)
                if (intent != null) permissionLauncher.launch(intent) else viewModel.onClickButtonConnect()
            },
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(50.dp),
            enabled = viewModel.isButtonConnectEnable.value,
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