package com.example.mobile_development_2_2.gui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import org.osmdroid.util.GeoPoint

data class GoalPoint(
    val location: GeoPoint,
    var name: String = "",
    var visited : MutableState<Boolean> = mutableStateOf(false)) {

}