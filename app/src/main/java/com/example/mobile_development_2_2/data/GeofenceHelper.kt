package com.example.mobile_development_2_2.data

import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.MutableState
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingRequest


class GeofenceHelper(base: Context?) : ContextWrapper(base) {
    private val TAG = "GeofenceHelper"
    private lateinit var pendingIntent: PendingIntent

    fun geofencingRequest  (geofence : Geofence) : GeofencingRequest? {
        Log.d(TAG, "requesting geofence")
        return GeofencingRequest.Builder()
            .addGeofence(geofence)
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .build()
    }

    fun getGeofence(lat : Double, lng : Double) : Geofence? {
        Log.d(TAG, "giving geofence")
        return Geofence.Builder()
            .setRequestId("geo")
            .setCircularRegion(lat, lng, 20f)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .build()
    }

    fun getPendingIntent() : PendingIntent? {
        Log.d(TAG, "pending intent")
        //Toast.makeText(this, "not init", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, GeofenceBroadcastReceiver::class.java)
        pendingIntent = PendingIntent.getBroadcast(this, 6969, intent, PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        return pendingIntent
    }

    fun getErrorString(e : Exception) : String? {
        Log.d(TAG, "geofence error")
        if (e is ApiException) {
            var apiException : ApiException = e
            when(apiException.statusCode) {
                GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE -> return "GEOFENCE_NOT_AVAILABLE"
                GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES -> return "GEOFENCE_TOO_MANY_GEOFENCES"
                GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS -> return "GEOFENCE_TOO_MANY_PENDING_INTENTS"
                GeofenceStatusCodes.GEOFENCE_INSUFFICIENT_LOCATION_PERMISSION -> return "GEOFENCE_INSUFFICIENT_LOCATION_PERMISSION"
            }
        }
        return e.localizedMessage
    }
}






















