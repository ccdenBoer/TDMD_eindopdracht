package com.example.mobile_development_2_2.gui

import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.PackageManagerCompat.LOG_TAG
import com.example.mobile_development_2_2.data.GeofenceHelper
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class GoalPointManager {
    var finished: MutableState<Boolean> = mutableStateOf(false)
    var started: MutableState<Boolean> = mutableStateOf(false)
    //private var goals: List<GoalPoint>? = null
    private val geoPoints = mutableListOf<GeoPoint>()
    private val goals = mutableListOf<GoalPoint>()
    var context: Context?
    private val TAG = "RouteManager"
    var amountOffGeofences = 6

    private constructor(context: Context?) {
        Log.d(LOG_TAG, "constructor")
        this.context = context
    }

    fun hasStarted(): Boolean {
        return started.value
    }

    fun start(location: MyLocationNewOverlay) {
        amountOffGeofences = 8
        generateGoals(location)
        started.value = true
    }

    fun stop() {
        removeAllGeofence()
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
        var g1 = GoalPoint(GeoPoint(location.myLocation.latitude + Math.random() / 100, location.myLocation.longitude + Math.random() / 100), "1");
        var g2 = GoalPoint(GeoPoint(location.myLocation.latitude + Math.random() / 100, location.myLocation.longitude + Math.random() / 100), "2");
        var g3 = GoalPoint(GeoPoint(location.myLocation.latitude + Math.random() / 100, location.myLocation.longitude - Math.random() / 100), "3");
        var g4 = GoalPoint(GeoPoint(location.myLocation.latitude + Math.random() / 100, location.myLocation.longitude - Math.random() / 100), "4");
        var g5 = GoalPoint(GeoPoint(location.myLocation.latitude - Math.random() / 100, location.myLocation.longitude + Math.random() / 100), "5");
        var g6 = GoalPoint(GeoPoint(location.myLocation.latitude - Math.random() / 100, location.myLocation.longitude + Math.random() / 100), "6");
        var g7 = GoalPoint(GeoPoint(location.myLocation.latitude - Math.random() / 100, location.myLocation.longitude - Math.random() / 100), "7");
        var g8 = GoalPoint(GeoPoint(location.myLocation.latitude - Math.random() / 100, location.myLocation.longitude - Math.random() / 100), "8");
//        var g7 = GoalPoint(GeoPoint(  51.5855817 , 4.789675), "6")
//        var g6 = GoalPoint(GeoPoint(37.4171833 , -122.202085),"7" )
//        var g8 = GoalPoint(GeoPoint(51.59437 , 4.7831083), "8")
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
            addGeofenceLocation(i.location.latitude, i.location.longitude, i.name)
        }


        return goals
    }

    fun addGeofenceLocation(lat: Double, lng: Double, id: String) {
        Log.d(LOG_TAG, "Setting geofence $lat $lng $id")
        var geofence: Geofence? = geofenceHelper?.getGeofence(lat, lng, id)

        var geofencingRequest: GeofencingRequest? = geofence?.let {
            geofenceHelper?.geofencingRequest(
                it
            )
        }
        var pendingIntent: PendingIntent? = geofenceHelper?.getPendingIntent()
        if (geofencingRequest != null) {
            if (pendingIntent != null) {
                geofencingClient?.addGeofences(geofencingRequest, pendingIntent)
                    ?.addOnSuccessListener {
                        Log.d(
                            LOG_TAG,
                            "Geofence added " + geofencingRequest.geofences[0].latitude + " " + geofencingRequest.geofences[0].longitude
                        )
                    }
                    ?.addOnFailureListener { e ->
                        Log.d(
                            ContentValues.TAG,
                            "onFailure: " + geofenceHelper?.getErrorString(e)
                        )
                    }
            }
        }
    }

    fun removeAllGeofence() {
        Log.d(TAG, "removed all geofences")
        geofenceHelper?.getPendingIntent()?.let { geofencingClient?.removeGeofences(it)  }
    }

    fun removeGeofence(id: String) {
        amountOffGeofences -= 1
        val geofenceIdsToRemove = mutableListOf(id)
        geofencingClient?.removeGeofences(geofenceIdsToRemove)?.run {
            addOnSuccessListener {
                Log.d(TAG, "Geofence with ID $id removed successfully.")
                Log.d(TAG, "$amountOffGeofences amount of active geofences left")
            }
            addOnFailureListener {
                Log.d(TAG, "Failed to remove geofence with ID $id: ${it.message}")
            }
        }
    }

    companion object {
        private var geofencingClient: GeofencingClient? = null
        private var geofenceHelper: GeofenceHelper? = null
        private var goalPointManager: GoalPointManager? = null

        fun getGoalPointManager(context: Context?): GoalPointManager {
            if (goalPointManager == null) {
                Log.d("RouteManager", "making goalpointmanager")
                geofenceHelper = GeofenceHelper(context)
                geofencingClient = context?.let { LocationServices.getGeofencingClient(it) }
                goalPointManager = GoalPointManager(context)
            }

            return goalPointManager as GoalPointManager
        }
    }
}