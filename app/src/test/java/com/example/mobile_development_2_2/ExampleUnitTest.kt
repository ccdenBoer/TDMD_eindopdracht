package com.example.mobile_development_2_2

import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.location.Location
import android.location.LocationProvider
import androidx.compose.runtime.remember
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.mobile_development_2_2.data.GeofenceBroadcastReceiver
import com.example.mobile_development_2_2.data.GeofenceHelper
import com.example.mobile_development_2_2.data.GoalTimer
import com.example.mobile_development_2_2.gui.GoalPoint
import com.example.mobile_development_2_2.gui.GoalPointManager
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import kotlinx.coroutines.delay
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

class UnitTest {

}

