package com.anushka.uilayerdemo1

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class CounterViewModel : ViewModel() {

    private val _screenState = mutableStateOf(
        MainScreenState(
        inputValue = "",
        displayingResult = "Total is 0.0"
    )
    )
    val screenState : State<MainScreenState> = _screenState

    //ui event
    private val _uiEventFlow = MutableSharedFlow<UIEvent>()
    val uiEventFlow = _uiEventFlow.asSharedFlow()

    private var total = 0.0

    //update state event function
    private fun addToTotal() {//總數由user從UI輸入的value加上去，轉為double(因爲上面是string)
        total += _screenState.value.inputValue.toDouble()
        _screenState.value = _screenState.value.copy(//用copy因為除了要更新的值之外，其他不變
            displayingResult = "Total is $total",
            isCountButtonVisible = false
        )
    }

    private fun resetTotal(){//重設
        total = 0.0
        _screenState.value = _screenState.value.copy(
            displayingResult = "Total is $total",
            inputValue = ""//input清空
        )
    }

    //Counter event handler
    fun onEvent(event: CounterEvent){//把Counter event當成像enum一樣使用，這就是sealed class用法
        when(event){
            is CounterEvent.ValueEntered -> {
                _screenState.value = _screenState.value.copy(//用copy因為除了要更新的值之外，其他不能變
                    inputValue = event.value,//從sealed class, data class參數取過來的
                    isCountButtonVisible = true
                )
            }
            is CounterEvent.CounterButtonClicked -> {
                addToTotal()
                viewModelScope.launch {
                    _uiEventFlow.emit(//負責通知event已完成，跳出提醒視窗
                        UIEvent.ShowMessage(
                            message = "Value added successfully"
                        )
                    )
                }
            }
            is CounterEvent.ResetButtonClicked -> {
                resetTotal()
                viewModelScope.launch {
                    _uiEventFlow.emit(//負責通知event已完成，跳出提醒視窗
                        UIEvent.ShowMessage(
                            message = "Value reset successfully"
                        )
                    )
                }
            }

        }
    }



}