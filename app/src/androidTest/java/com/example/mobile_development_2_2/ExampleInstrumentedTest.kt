package com.example.mobile_development_2_2

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.mobile_development_2_2.data.GeofenceBroadcastReceiver
import com.example.mobile_development_2_2.data.GeofenceHelper
import com.example.mobile_development_2_2.data.GoalTimer
import com.example.mobile_development_2_2.gui.GoalPoint
import com.example.mobile_development_2_2.gui.GoalPointManager
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.delay

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before
import org.osmdroid.util.GeoPoint

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    private lateinit var geofenceHelper: GeofenceHelper
    private lateinit var context: Context
    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        geofenceHelper = GeofenceHelper(context)
    }
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.mobile_development_2_2", appContext.packageName)
    }
    @Test
    fun testGeofencingRequest() {
        val geofence = Geofence.Builder()
            .setRequestId("Test Geofence")
            .setCircularRegion(0.0, 0.0, 100f)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
            .build()

        val request = geofenceHelper.geofencingRequest(geofence)

        assertEquals(GeofencingRequest.INITIAL_TRIGGER_ENTER, request?.initialTrigger)
        assertEquals(1, request?.geofences?.size)
        assertEquals(geofence.requestId, request?.geofences?.get(0)?.requestId)
    }

    @Test
    fun testGetGeofence() {
        val lat = 1.0
        val lng = 2.0
        val id = "Test Geofence"
        val geofence = geofenceHelper.getGeofence(lat, lng, id)

        assertEquals(id, geofence?.requestId)
        assertEquals(lat, geofence?.latitude)
        assertEquals(lng, geofence?.longitude)
        assertEquals(20f, geofence?.radius)
        assertEquals(Geofence.GEOFENCE_TRANSITION_ENTER, geofence?.transitionTypes)
        assertEquals(Geofence.NEVER_EXPIRE, geofence?.expirationTime)
    }

//    @Test
//    fun testGetPendingIntent() {
//        val pendingIntent = geofenceHelper.getPendingIntent()
//        assertEquals(GeofenceBroadcastReceiver::class.java, pendingIntent?.creatorPackage)
//    }

    @Test
    fun happy_timer() {
        GoalTimer.start()
        Thread.sleep(1000)
        assert(GoalTimer.secondsPassed.value > 500.0 && GoalTimer.secondsPassed.value < 1500.0)
    }
//    @Test
//    fun happy_goals() {
//        GoalPointManager.getGoalPointManager(null).setGoals(
//            listOf(
//                GoalPoint(GeoPoint(37.7749, -122.4194)),
//                GoalPoint(GeoPoint(40.7128, -74.0060)),
//                GoalPoint(GeoPoint(51.5074, -0.1278))
//            )
//        )
//        assert(GoalPointManager.getGoalPointManager(null).getGoals().size == 3)
//    }
//    @Test
//    fun generateGoals() {
//        mockLocation.myLocation.setCoords(51.806917655901294, 5.577502200540777)
//        GoalPointManager.getGoalPointManager(null).generateGoals(mockLocation)
//        //check if all goals are generated within ~1km of the current location
//        for(goal in GoalPointManager.getGoalPointManager(null).getGoals())  {
//            assert((mockLocation.myLocation.latitude > goal.location.latitude -0.01 ||
//                    mockLocation.myLocation.latitude < goal.location.latitude +0.01) &&
//                    (mockLocation.myLocation.longitude > goal.location.longitude -0.01 ||
//                    mockLocation.myLocation.longitude < goal.location.longitude +0.01))
//
//        }
//    }

}