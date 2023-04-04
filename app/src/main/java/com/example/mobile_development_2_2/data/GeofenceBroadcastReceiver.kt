package com.example.mobile_development_2_2.data

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.example.mobile_development_2_2.map.route.POI
import com.example.mobile_development_2_2.gui.MainActivity
import com.example.mobile_development_2_2.gui.fragments.MapFragment
import com.example.mobile_development_2_2.map.route.Route
import com.example.mobile_development_2_2.map.route.RouteManager
import com.google.android.gms.location.*

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    private val TAG = "GeofenceBroadcastReceiver"

    override fun onReceive(context: Context, intent: Intent) {
        var notificationHelper = NotificationHelper(context)


        //Toast.makeText(context, "geofence triggered", Toast.LENGTH_SHORT).show()
        Log.d(TAG, "onReceive: geofence triggered")

        var geofencingEvent : GeofencingEvent? = GeofencingEvent.fromIntent(intent)

        if (geofencingEvent != null) {
            if(geofencingEvent.hasError()) {
                Log.d(TAG, "onReceive: Error geofencing event")
            }
        }

        var geofenceList : List<Geofence>? = geofencingEvent?.triggeringGeofences

        if (geofenceList != null) {
            for (geofence in geofenceList) {
                Log.d(TAG, "onReceive: " + geofence.requestId + " triggered ")
                PopupHelper.SetState(true)

                val poi = RouteManager.getRouteManager(null).get_CurrentPoi()
                var description = poi.shortDescription

                notificationHelper.sendHighPriorityNotification(RouteManager.getRouteManager(null).getStringByName("notification") + " " + poi.name, "", MainActivity::class.java)
                RouteManager.getRouteManager(null).triggeredGeofence()
                RouteManager.getRouteManager(null).selectedRoute.totalPoisVisited.value++

            }
        }
    }
}
