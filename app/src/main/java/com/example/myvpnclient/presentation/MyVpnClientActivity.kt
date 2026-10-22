package com.example.myvpnclient.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.myvpnclient.MyApp
import androidx.lifecycle.viewmodel.compose.viewModel


class MyVpnClientActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        (application as MyApp).appComponent.inject(this)

        val factoryForMainScreen = (application as MyApp).appComponent
            .mainScreenViewModelFactory()

        setContent {
            MainScreen(viewModel = viewModel(factory = factoryForMainScreen))
        }
    }
}
