package com.example.mobile_development_2_2.gui

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class GoalPointManager {
    var finished: MutableState<Boolean> = mutableStateOf(false)
    var started: MutableState<Boolean> = mutableStateOf(false)
    //private var goals: List<GoalPoint>? = null
    private val geoPoints = mutableListOf<GeoPoint>()
    private val goals = mutableListOf<GoalPoint>()

    private constructor(){
    }

    fun hasStarted(): Boolean {
        return started.value
    }

    fun start(location: MyLocationNewOverlay) {
        generateGoals(location)
        started.value = true

    }

    fun stop() {
        geoPoints.clear()
        goals.clear()
        started.value = false
    }

    fun getGoals(): MutableList<GoalPoint> {
        if (!goals.isEmpty()) {
            return goals
        } else {
            return testList()
        }
    }

    fun getGoalsAsGeoPoint(): MutableList<GeoPoint> {

        return geoPoints
    }

    private fun testList(): MutableList<GoalPoint> {
        var a = GoalPoint(GeoPoint(47, 6));
        var b = GoalPoint(GeoPoint(50, 50));
        var c = GoalPoint(GeoPoint(50, 50));

        return mutableListOf<GoalPoint>(a, b, c)
    }

    fun generateGoals(location: MyLocationNewOverlay): MutableList<GoalPoint> {
        Log.d("GEOPOINTMANAGER", "${location.myLocation.latitude} - ${location.myLocation.longitude}")
        var g1 = GoalPoint(GeoPoint(location.myLocation.latitude + Math.random() / 200, location.myLocation.longitude + Math.random() / 200));
        var g2 = GoalPoint(GeoPoint(location.myLocation.latitude + Math.random() / 200, location.myLocation.longitude + Math.random() / 200));
        var g3 = GoalPoint(GeoPoint(location.myLocation.latitude + Math.random() / 200, location.myLocation.longitude - Math.random() / 200));
        var g4 = GoalPoint(GeoPoint(location.myLocation.latitude + Math.random() / 200, location.myLocation.longitude - Math.random() / 200));
        var g5 = GoalPoint(GeoPoint(location.myLocation.latitude - Math.random() / 200, location.myLocation.longitude + Math.random() / 200));
        var g6 = GoalPoint(GeoPoint(location.myLocation.latitude - Math.random() / 200, location.myLocation.longitude + Math.random() / 200));
        var g7 = GoalPoint(GeoPoint(location.myLocation.latitude - Math.random() / 200, location.myLocation.longitude - Math.random() / 200));
        var g8 = GoalPoint(GeoPoint(location.myLocation.latitude - Math.random() / 200, location.myLocation.longitude - Math.random() / 200));

        goals.add(g1)
        goals.add(g2)
        goals.add(g3)
        goals.add(g4)
        goals.add(g5)
        goals.add(g6)
        goals.add(g7)
        goals.add(g8)

        goals.forEach { i ->
            geoPoints.add(i.location)
        }


        return goals
    }

    companion object {
        private var goalPointManager: GoalPointManager? = null

        fun getGoalPointManager(): GoalPointManager {
            if (goalPointManager == null) {
                goalPointManager = GoalPointManager()
            }

            return goalPointManager as GoalPointManager
        }
    }
}