package com.example.myvpnclient.presentation

import android.app.Application
import android.content.pm.ApplicationInfo
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myvpnclient.domain.ActivateVpnUseCase
import com.example.myvpnclient.domain.SavePreferenceUseCase
import com.example.myvpnclient.domain.GetListInstalledApplicationUseCase
import com.example.myvpnclient.domain.GetPreferenceUseCase
import com.example.myvpnclient.domain.InactivateVpnUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainScreenViewModel(
    val application: Application,
    val activateVpnUseCase: ActivateVpnUseCase,
    val inactivateVpnUseCase: InactivateVpnUseCase,
    val getListInstalledApplicationUseCase: GetListInstalledApplicationUseCase,
    val savePreferenceUseCase: SavePreferenceUseCase,
    val getPreferenceUseCase: GetPreferenceUseCase
) :
    ViewModel() {

    var job: Job? = null
    private val _valueForAuthCodeTextField = mutableStateOf(TextFieldValue(text = ""))
    val valueForAuthCodeTextField: State<TextFieldValue> = _valueForAuthCodeTextField

    private val _valueForServerHostTextField = mutableStateOf(TextFieldValue(text = ""))
    val valueForServerHostTextField: State<TextFieldValue> = _valueForServerHostTextField

    private val _valueForServerPortTextField = mutableStateOf(TextFieldValue(text = ""))
    val valueForServerPortTextField: State<TextFieldValue> = _valueForServerPortTextField

    private val _isFormValid = mutableStateOf(false)
    val isFormValid: State<Boolean> = _isFormValid

    private val _valueForDropDown = mutableStateOf<List<ApplicationInfo>>(emptyList())

    val valueForDropDown: State<List<ApplicationInfo>> = _valueForDropDown

    private val _isCheckBoxActive = mutableStateListOf<Boolean>()

    val isCheckBoxActive: List<Boolean> = _isCheckBoxActive

    init {
        viewModelScope.launch {
            Log.d("VPNLog","я в инит блоке")
            _valueForDropDown.value = getListInstalledApplicationUseCase.invoke(application)
            _isCheckBoxActive.clear()
            _isCheckBoxActive.addAll(List(_valueForDropDown.value.size) { false })
            getPreferenceUseCase.invoke().first().let {
                _valueForAuthCodeTextField.value = TextFieldValue(text = it.authCode)
                Log.d("VPNLog", it.authCode)
                _valueForServerHostTextField.value = TextFieldValue(text = it.host)
                Log.d("VPNLog", it.host)
                _valueForServerPortTextField.value = TextFieldValue(text = it.port)
                Log.d("VPNLog", it.port)
                _isCheckBoxActive.indices.forEach { i ->
                    _isCheckBoxActive[i] = it.checkBoxState.getOrNull(i) ?: false
                }
            }
        }
    }

    fun onAuthCodeSymbolChanged(newValue: TextFieldValue) {
        _valueForAuthCodeTextField.value = newValue
        _isFormValid.value = validate()
        savePreference("auth_code", _valueForAuthCodeTextField.value.text)
    }

    fun onServerHostSymbolChanged(newValue: TextFieldValue) {
        _valueForServerHostTextField.value = newValue
        _isFormValid.value = validate()
        savePreference("server_host", _valueForServerHostTextField.value.text)
    }

    fun onServerPortSymbolChanged(newValue: TextFieldValue) {
        _valueForServerPortTextField.value = newValue
        _isFormValid.value = validate()
        savePreference("server_port", _valueForServerPortTextField.value.text)
    }

    private fun validate(): Boolean =
        _valueForServerPortTextField.value.text != "" && _valueForServerHostTextField.value.text != "" && _valueForAuthCodeTextField.value.text != "" && !(job?.isActive
            ?: false)

    fun onClickButtonConnect() {
        if (_isFormValid.value) {
            job = viewModelScope.launch {
                val allowedPackageList = mutableListOf("")
                _valueForDropDown.value.forEachIndexed { index, info ->
                    if (_isCheckBoxActive[index]) {
                        allowedPackageList.add(info.packageName)
                    }
                }
                activateVpnUseCase.invoke(
                    _valueForAuthCodeTextField.value.text,
                    _valueForServerHostTextField.value.text,
                    _valueForServerPortTextField.value.text.toInt(),
                    application,
                    ArrayList(allowedPackageList)
                )
            }
        } else {
            Toast.makeText(application, "Не все поля заполнены!", Toast.LENGTH_SHORT).show()
        }
    }

    fun onClickButtonDisConnect() {
        viewModelScope.launch {
            inactivateVpnUseCase.invoke(application)
        }
    }

    fun onCheckedChange(index: Int, name: String) {
        _isCheckBoxActive[index] = !_isCheckBoxActive[index]
        savePreference(name, _isCheckBoxActive[index])
    }

    private fun <T : Any> savePreference(key: String, value: T) {
        viewModelScope.launch {
            Log.d("VPNLog", key)
            savePreferenceUseCase.invoke(key, value)
        }
    }
}

internal class MainScreenViewModelFactory @Inject constructor(
    val application: Application,
    val activateVpnUseCase: ActivateVpnUseCase,
    val inactivateVpnUseCase: InactivateVpnUseCase,
    val getListInstalledApplicationUseCase: GetListInstalledApplicationUseCase,
    val savePreferenceUseCase: SavePreferenceUseCase,
    val getPreferenceUseCase: GetPreferenceUseCase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        return MainScreenViewModel(
            application = application,
            activateVpnUseCase = activateVpnUseCase,
            inactivateVpnUseCase = inactivateVpnUseCase,
            getListInstalledApplicationUseCase = getListInstalledApplicationUseCase,
            savePreferenceUseCase = savePreferenceUseCase,
            getPreferenceUseCase = getPreferenceUseCase
        ) as T
    }
}