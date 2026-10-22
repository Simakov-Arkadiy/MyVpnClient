package com.example.myvpnclient.presentation

import android.app.Application
import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myvpnclient.domain.ActivateVpnUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainScreenViewModel(val application: Application, val useCase: ActivateVpnUseCase) :
    ViewModel() {
    private val _valueForAuthCodeTextField = mutableStateOf(TextFieldValue(text = ""))
    val valueForAuthCodeTextField: State<TextFieldValue> = _valueForAuthCodeTextField

    private val _valueForServerHostTextField = mutableStateOf(TextFieldValue(text = ""))
    val valueForServerHostTextField: State<TextFieldValue> = _valueForServerHostTextField

    private val _valueForServerPortTextField = mutableStateOf(TextFieldValue(text = ""))
    val valueForServerPortTextField: State<TextFieldValue> = _valueForServerPortTextField

    private val _isFormValid = mutableStateOf(false)

    val isFormValid: State<Boolean> = _isFormValid

    fun onAuthCodeSymbolChanged(newValue: TextFieldValue) {
        _valueForAuthCodeTextField.value = newValue
        _isFormValid.value =
            _valueForServerPortTextField.value.text != "" && _valueForServerHostTextField.value.text != ""
    }

    fun onServerHostSymbolChanged(newValue: TextFieldValue) {
        _valueForServerHostTextField.value = newValue
        _isFormValid.value =
            _valueForServerPortTextField.value.text != "" && _valueForAuthCodeTextField.value.text != ""
    }

    fun onServerPortSymbolChanged(newValue: TextFieldValue) {
        _valueForServerPortTextField.value = newValue
        _isFormValid.value =
            _valueForAuthCodeTextField.value.text != "" && _valueForAuthCodeTextField.value.text != ""
    }

    fun onClickButtonConnect() {
        if(_isFormValid.value){
            viewModelScope.launch {
                useCase.invoke(
                    _valueForAuthCodeTextField.value.text,
                    _valueForServerHostTextField.value.text,
                    _valueForServerPortTextField.value.text.toInt(),
                    application
                )
            }
        }
        else{
            Toast.makeText(application,"Не все поля заполнены!",Toast.LENGTH_SHORT).show()
        }
    }
}

internal class MainScreenViewModelFactory @Inject constructor(
    val application: Application, val useCase: ActivateVpnUseCase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        return MainScreenViewModel(
            application = application,
            useCase = useCase
        ) as T
    }
}