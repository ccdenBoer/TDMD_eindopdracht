package com.example.mobile_development_2_2.map.gps


import android.content.Context
import android.location.Location
import com.google.android.gms.location.*

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*

import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class GPSLocationProvider (context : Context) {
    private val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    suspend fun lastLocation(): Result<Location> = last()
    fun locationTracker(): Flow<Result<Location>> = track()

    private suspend fun last(): Result<Location> = suspendCoroutine { continuation ->
        fusedLocationProviderClient.lastLocation
            .addOnSuccessListener {
                // Note: it is of type Location! a platform type that can be null
                if (it !== null) {
                    continuation.resume(Result.success(it))
                } else {
                    continuation.resume(Result.failure(Exception("No location available")))
                }
            }
            .addOnFailureListener {
                continuation.resume(Result.failure(it))
            }
    }

    private fun track(
    ): Flow<Result<Location>> = callbackFlow {
        val locationRequest = LocationRequest.Builder(500)
            .setGranularity(Granularity.GRANULARITY_FINE)
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .setWaitForAccurateLocation(true)
            .setMaxUpdateAgeMillis(LocationRequest.Builder.IMPLICIT_MAX_UPDATE_AGE)
            .setMinUpdateIntervalMillis(LocationRequest.Builder.IMPLICIT_MIN_UPDATE_INTERVAL)
            .build()
        val locationListener = LocationListener { location -> trySend(Result.success(location)) }

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationListener, null)

        awaitClose {
            fusedLocationProviderClient.removeLocationUpdates(locationListener)
        }
    }.catch {
        emit(Result.failure(it))
    }
}
class GetLocationProvider(
    private val locationprovider: GPSLocationProvider,
) {
    operator fun invoke() = locationprovider.locationTracker()
        .map { it.getOrNull() }
        .filterNotNull()
}