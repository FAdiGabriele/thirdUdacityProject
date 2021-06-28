package com.udacity.uicomponent


sealed class ButtonState {
    object Inactive : ButtonState()
    object Clicked : ButtonState()
    object Loading : ButtonState()
    object Completed : ButtonState()
}