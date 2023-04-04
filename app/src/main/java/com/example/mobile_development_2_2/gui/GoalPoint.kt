package com.example.mobile_development_2_2.gui

import org.osmdroid.util.GeoPoint

data class GoalPoint(
    val location: GeoPoint,
    var visited : Boolean = false,
    var name: String = ""
    ) {

}