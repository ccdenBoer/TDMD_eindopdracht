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
    private lateinit var geofencingClient : GeofencingClient

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        geofenceHelper = GeofenceHelper(context)
        GoalPointManager.getGoalPointManager(context)
        geofencingClient = LocationServices.getGeofencingClient(context)
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

    @Test
    fun happy_timer() {
        assert(!GoalTimer.started.value) {"${GoalTimer.started.value} : false"}
        GoalTimer.start()
        Thread.sleep(3000)
        assert(GoalTimer.started.value)
        assert(GoalTimer.getSecondPassed().value > 2.9 && GoalTimer.getSecondPassed().value < 3.1){"${GoalTimer.getSecondPassed().value} : 3"}
        assert(GoalTimer.started.value) {"${GoalTimer.started.value} : true"}
    }

    @Test
    fun unhappy_timer() {
        GoalTimer.start()
        Thread.sleep(2000)
        GoalTimer.start()
        //assert(GoalTimer.started.value) {"${GoalTimer.started.value} : true"}
        Thread.sleep(1000)
        assert(GoalTimer.getSecondPassed().value > 0.9 && GoalTimer.getSecondPassed().value < 1.1){"${GoalTimer.getSecondPassed().value} : 1"}
    }

    @Test
    fun happy_addRemoveGeofence() {
        GoalPointManager.getGoalPointManager(null).addGeofenceLocation(0.0, 0.0, "test")
        GoalPointManager.getGoalPointManager(null).addGeofenceLocation(1.0, 1.0, "test2")
        GoalPointManager.getGoalPointManager(null).addGeofenceLocation(2.0, 2.0, "test3")

        Thread.sleep(300)
        val activeGeofences = GoalPointManager.getGoalPointManager(null).activeGeofences
        assert(activeGeofences.size == 3) {
            "Expected 3 geofences, but got ${activeGeofences.size}"
        }
        assert(activeGeofences.map { it }.containsAll(listOf("test", "test2", "test3"))) {
            "Expected geofences with IDs 'test', 'test2', and 'test3', but got ${activeGeofences.map { it }}"
        }

        GoalPointManager.getGoalPointManager(null).removeGeofence("test")
        Thread.sleep(100)

        val activeGeofencesAfterRemoval = GoalPointManager.getGoalPointManager(null).activeGeofences
        assert(activeGeofencesAfterRemoval.size == 2) {
            "Expected 2 geofences, but got ${activeGeofencesAfterRemoval.size}"
        }
        assert(activeGeofencesAfterRemoval.map { it }.containsAll(listOf("test2", "test3"))) {
            "Expected geofences with IDs 'test2' and 'test3', but got ${activeGeofencesAfterRemoval.map { it }}"
        }

        GoalPointManager.getGoalPointManager(null).removeAllGeofence()
        Thread.sleep(100)
        assert(GoalPointManager.getGoalPointManager(null).activeGeofences.isEmpty()) {
            "Expected no geofences, but got ${GoalPointManager.getGoalPointManager(null).activeGeofences.size}"
        }
    }

    @Test
    fun unhappy_addRemoveGeofence() {
        GoalPointManager.getGoalPointManager(null).addGeofenceLocation(0.0, 0.0, "test")
        GoalPointManager.getGoalPointManager(null).addGeofenceLocation(0.0, 0.0, "test")
        Thread.sleep(300)

        assert(GoalPointManager.getGoalPointManager(null).activeGeofences.size == 1) {
            "${GoalPointManager.getGoalPointManager(null).activeGeofences.size} : 1"
        }

        val activeGeofenceIds = GoalPointManager.getGoalPointManager(null).activeGeofences.map { it }
        assert(activeGeofenceIds == listOf("test")) {
            "$activeGeofenceIds : [test]"
        }

        GoalPointManager.getGoalPointManager(null).removeGeofence("")
        Thread.sleep(100)

        assert(GoalPointManager.getGoalPointManager(null).activeGeofences.size == 1) {
            "${GoalPointManager.getGoalPointManager(null).activeGeofences.size} : 1"
        }

        val activeGeofenceIdsAfterRemove = GoalPointManager.getGoalPointManager(null).activeGeofences.map { it }
        assert(activeGeofenceIdsAfterRemove == listOf("test")) {
            "$activeGeofenceIdsAfterRemove : [test]"
        }
    }






}