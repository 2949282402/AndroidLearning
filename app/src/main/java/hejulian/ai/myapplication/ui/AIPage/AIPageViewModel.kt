package hejulian.ai.myapplication.ui.AIPage

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AIPageViewModel: ViewModel() {
    private val _inputText = MutableStateFlow("")
    val inputText = _inputText.asStateFlow()

    fun onInputTextChange(value: String){
        _inputText.value = value
    }

    fun clearInputText(){
        _inputText.value = ""
    }

}