package com.example.mobile_development_2_2.map.route

import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import com.example.mobile_development_2_2.R
import com.example.mobile_development_2_2.data.GeofenceHelper
import com.example.mobile_development_2_2.data.Lang
import com.example.mobile_development_2_2.gui.MainActivity
import com.example.mobile_development_2_2.map.route.Route.Companion.TestRoute

import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import java.io.File
import java.nio.charset.Charset

class RouteManager {
    val LOG_TAG = "RouteManager"

    var routes: List<Route>

    var context: Context?
    lateinit var selectedRoute: Route
    lateinit var targetPOI: POI
    lateinit var previousTargetPOI: POI
    private val TAG = "RouteManager"

    private constructor(context: Context?) {
        Log.d(LOG_TAG, "constructor")
        this.context = context
        routes = GenerateRoutes()


        loadRoutesProgress()

        routes.forEach {
            it.totalPoisVisited = mutableStateOf(it.totalPoisVisited())
            it.currentLength = mutableStateOf(it.getTotalLength())
            it.finished = mutableStateOf(it.totalPoisVisited.value == it.POIs.size)
        }

        selectedRoute = routes.get(0)
        previousTargetPOI = routes.get(0).POIs.get(0)
    }


    fun GenerateRoutes(): List<Route> {
        Log.d(LOG_TAG, "generating routes")
        val jsonString: String =
            context?.resources!!.openRawResource(R.raw.historische_kilometer).bufferedReader()
                .use { it.readText() }
        val gson = Gson()
        val routes = gson.fromJson(jsonString, Array<Route>::class.java).toList()
        for (it in routes) {
            it.started = mutableStateOf(false)
            it.totalPoisVisited = mutableStateOf(0)
            it.currentLength = mutableStateOf(0.0)
            it.finished = mutableStateOf(false)
            for(poi in it.POIs){
                poi.visited = false

                //poi.shortDescription = getStringById(poi.shortDescription)

                if (poi.imgMap == null){
                    poi.imgMap = "ic_logo.png"
                }
                if (poi.img == null){
                    poi.img = "notfound404.png"
                }
            }
        }
        return routes
    }

    fun GetRoutes(): List<Route> {
        Log.d(LOG_TAG, "giving routes")
        return routes
    }


    fun setRouteState(started: Boolean) {
        Log.d(LOG_TAG, "setting route state")
        getRouteByName(selectedRoute.name)?.started?.value = started
    }

    fun getRouteByName(name: String): Route? {
        Log.d(LOG_TAG, "giving route by name")
        for (route in routes) {
            if (route.name == name)
                return route
        }
        return null
    }

    fun selectRoute(route: Route) {
        Log.d(LOG_TAG, "route selected")
        for (poi in route.POIs) {
            if(!poi.visited){
                targetPOI = poi
                break
            }
        }
        selectedRoute = route
    }

    @JvmName("getSelectedItem1")
    fun getSelectedRoute(): Route {
        Log.d(LOG_TAG, "gicing selected route")
        return selectedRoute
    }

    fun get_CurrentPoi(): POI {
        Log.d(LOG_TAG, "giving current poi")
        return targetPOI
    }

    fun selectPOI(poi: POI) {
        Log.d(LOG_TAG, "selecting poi")
        Route.selectItem(poi)
    }

    fun getSelectedPOI(): POI {
        Log.d(LOG_TAG, "giving selected poi")
        return Route.getSelectedPOI()
    }

    @JvmName("getTargetPOI1")
    fun getTargetPOI(): POI{
        Log.d(LOG_TAG, "giving target poi")
        return targetPOI
    }

    @JvmName("setTargetPOI1")
    fun setTargetPOI(poi: POI){
        Log.d(LOG_TAG, "setting target poi")
        targetPOI = poi
    }

    fun getStringByName(idName: String): String {
        Log.d(LOG_TAG, "getting strtring resource by name")
        return Lang.get(context?.resources!!.getIdentifier(idName, "string", context?.packageName))
    }

    fun triggeredGeofence() {
        Log.d(LOG_TAG, "Geofence triggered")
        //set visited poi true
        for (poi in selectedRoute.POIs) {
            Log.d(LOG_TAG, "checking poi status first")
            if (!poi.visited) {
                poi.visited = true
                break
            }
        }

        goToNextGeofence()

    }

    fun goToNextGeofence(){
        //get next poi in route
        var routeFinished = true
        for (poi in selectedRoute.POIs) {
            Log.d(LOG_TAG, "checking poi status")
            if (!poi.visited) {
                Log.d(LOG_TAG, "unfinished poi found")
                targetPOI = poi
                setGeofenceLocation(poi.location.latitude, poi.location.longitude)
                routeFinished = false
                break
            }
            previousTargetPOI = poi
        }
        if(routeFinished) {
            //route finished
        }
    }

    fun setGeofenceLocation(lat: Double, lng: Double) {
        Log.d(LOG_TAG, "Setting geofence $lat $lng")
        var geofence: Geofence? = geofenceHelper?.getGeofence(lat, lng)

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
                        Log.d(
                            LOG_TAG,
                            "Geofence test " + geofenceHelper?.getGeofence(geofencingRequest.geofences[0].latitude, geofencingRequest.geofences[0].longitude)
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


    fun removeGeofence() {
        Log.d(TAG, "removeGeofence")
        geofenceHelper?.getPendingIntent()?.let { geofencingClient?.removeGeofences(it)  }
    }

    fun saveRoutesProgress(){
        val resDir = context?.getDir("CHLAM", Context.MODE_PRIVATE)
        File(resDir, "routesProgress.txt").createNewFile()

        File(resDir, "routesProgress.txt").printWriter().use { out ->
            routes.forEach {
                it.POIs.forEach{
                    out.println(it.visited.toString())
                }
            }
        }
    }

    fun loadRoutesProgress(){
        val resDir = context?.getDir("CHLAM", Context.MODE_PRIVATE)
        if(File(resDir, "routesProgress.txt").exists()){
            File(resDir, "routesProgress.txt").reader(Charset.defaultCharset()).use { re ->
                val lines = re.readLines()
                var i = 0
                routes.forEach {
                    it.POIs.forEach{
                        if(lines.size > i){
                            Log.d(LOG_TAG, lines[i])
                            it.load(lines[i])
                        }

                        i++
                    }
                }
            }
        }



    }


    companion object {
        private var routeManager: RouteManager? = null
        private var geofencingClient: GeofencingClient? = null
        private var geofenceHelper: GeofenceHelper? = null

        private fun TestRoutes(): List<Route> {
            Log.d("RouteManager", "testroutes")
            var testRoute1 = Route.TestRoute("testRoute1")
            var testRoute2 = Route.TestRoute2("testRoute2")
            //var testRoute3 = Route.TestRoute("testRoute3")

            var routes = listOf<Route>(
                testRoute1,
                testRoute2,
                //testRoute3
            )

            return routes
        }

        fun getRouteManager(context: Context?): RouteManager {
            Log.d("RouteManager", "getroutemanager")

            if (routeManager == null) {
                Log.d("RouteManager", "making routemanager")
                routeManager = RouteManager(context)
                geofenceHelper = GeofenceHelper(context)
                geofencingClient = context?.let { LocationServices.getGeofencingClient(it) }
            }
            return routeManager as RouteManager
        }


    }
}