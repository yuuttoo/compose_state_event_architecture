package com.anushka.uilayerdemo1

sealed class CounterEvent{
    data class ValueEntered(val value : String) : CounterEvent()
    object CounterButtonClicked : CounterEvent()
    object ResetButtonClicked : CounterEvent()
}