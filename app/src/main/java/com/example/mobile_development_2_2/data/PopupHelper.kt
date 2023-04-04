package com.example.mobile_development_2_2.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

class PopupHelper {
    companion object{
        var popupBool = mutableStateOf(false)

        fun SetState(popup: MutableState<Boolean>){
            popupBool = popup
        }

        fun SetState(state: Boolean){
            popupBool.value = state
        }
    }
}