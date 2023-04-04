package com.example.mobile_development_2_2.gui

import org.osmdroid.util.GeoPoint

data class GoalPoint(
    val location: GeoPoint,
    var name: String = "",
    var visited : Boolean = false) {

}