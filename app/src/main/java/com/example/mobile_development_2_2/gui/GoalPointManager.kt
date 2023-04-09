package com.example.mobile_development_2_2.gui

import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.PackageManagerCompat.LOG_TAG
import com.example.mobile_development_2_2.data.*
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.time.LocalDateTime

class GoalPointManager {
    var finished: MutableState<Boolean> = mutableStateOf(false)
    var started: MutableState<Boolean> = mutableStateOf(false)
    var goalsVisited: MutableState<Int> = mutableStateOf(0)
    private var geoPoints = mutableListOf<GeoPoint>()
    private var goals = mutableListOf<GoalPoint>()
    var context: Context?
    private val TAG = "GoalPointManager"
    var amountOffGeofences = 8
    private val totalPoints = 8

    private constructor(context: Context?) {
        Log.d(LOG_TAG, "constructor")
        this.context = context
    }

    fun hasStarted(): Boolean {
        return started.value
    }

    fun hasFinished(): Boolean {
        return finished.value
    }

    fun start(location: MyLocationNewOverlay) {
        amountOffGeofences = 8
        generateGoals(location)
        started.value = true
        finished.value = false
    }

    fun stop() {
        removeAllGeofence()
        geoPoints = emptyList<GeoPoint>().toMutableList()
        goals = emptyList<GoalPoint>().toMutableList()
        goalsVisited.value = 0
        started.value = false

    }

    fun getGoals(): MutableList<GoalPoint> {
        if(goals.isEmpty()){
            goals = emptyList<GoalPoint>().toMutableList()
        }
        return goals
    }

    fun totalPointsVisited(): MutableState<Int> {
        goalsVisited.value = 0
        goals.forEach { i ->
            if(i.visited.value){
                goalsVisited.value++
            }
        }
        return goalsVisited
    }

    fun totalPoints(): Int{
        return totalPoints
    }

    fun getGoalsAsGeoPoint(): MutableList<GeoPoint> {

        return geoPoints
    }

    private fun testList(): MutableList<GoalPoint> {
        var a = GoalPoint(GeoPoint(47, 6));
        var b = GoalPoint(GeoPoint(50, 80));
        var c = GoalPoint(GeoPoint(80, 50));

        return mutableListOf<GoalPoint>(a, b, c)
    }

    fun generateGoals(location: MyLocationNewOverlay): MutableList<GoalPoint> {
        Log.d("GEOPOINTMANAGER", "${location.myLocation.latitude} - ${location.myLocation.longitude}")
        var g1 = GoalPoint(GeoPoint(location.myLocation.latitude + Math.random() / 1000, location.myLocation.longitude + Math.random() / 1000), "1");
        var g2 = GoalPoint(GeoPoint(location.myLocation.latitude + Math.random() / 1000, location.myLocation.longitude + Math.random() / 1000), "2");
        var g3 = GoalPoint(GeoPoint(location.myLocation.latitude + Math.random() / 1000, location.myLocation.longitude - Math.random() / 1000), "3");
        var g4 = GoalPoint(GeoPoint(location.myLocation.latitude + Math.random() / 1000, location.myLocation.longitude - Math.random() / 1000), "4");
        var g5 = GoalPoint(GeoPoint(location.myLocation.latitude - Math.random() / 1000, location.myLocation.longitude + Math.random() / 1000), "5");
        var g6 = GoalPoint(GeoPoint(location.myLocation.latitude - Math.random() / 1000, location.myLocation.longitude + Math.random() / 1000), "6");
        var g7 = GoalPoint(GeoPoint(location.myLocation.latitude - Math.random() / 1000, location.myLocation.longitude - Math.random() / 1000), "7");
        var g8 = GoalPoint(GeoPoint(location.myLocation.latitude - Math.random() / 1000, location.myLocation.longitude - Math.random() / 1000), "8");
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
            Thread{
                addGeofenceLocation(i.location.latitude, i.location.longitude, i.name)
            }.start()

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
                goals.forEach(){gp ->
                    //Log.d(TAG, "comparing ${gp.name} and ${id}")
                    if(gp.name.equals(id)){
                        Log.d(TAG, "visited $gp.name")
                        gp.visited.value = true
                    }
                }
                totalPointsVisited()
                if(goalsVisited.value >= totalPoints){
                    finished.value = true
                    var date = LocalDateTime.now().toString()
                    Log.d(TAG, "Adding win")
                    getTotalWinsFromDatabase(GoalDatabase.getInstance(context!!)){
                        Log.d(TAG, "Adding win for real id: ${it+1}")
                        addWinsFromDatabase(GoalDatabase.getInstance(context!!), GoalDatabase.Win(it+1, date, GoalTimer.secondsPassed.value))
                    }

                }
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