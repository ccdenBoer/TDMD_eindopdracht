package com.example.mobile_development_2_2

import android.content.Context
import android.location.Location
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.ui.text.font.emptyCacheFontFamilyResolver
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
import okhttp3.internal.wait
import org.apache.commons.lang3.ObjectUtils.Null

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.lang.Thread.sleep

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    private lateinit var geofenceHelper: GeofenceHelper
    private lateinit var context: Context
    private lateinit var mapView: MapView
    private lateinit var myLocationNewOverlay: MyLocationNewOverlay
    private lateinit var location: Location

    @Before
    fun before() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        geofenceHelper = GeofenceHelper(context)
        val handler = Handler(Looper.getMainLooper())
        var finished = false
        handler.post(){
            mapView = MapView(this.context)
            myLocationNewOverlay = MyLocationNewOverlay(mapView)

            location = Location("mock")
            location.latitude = 50.0
            location.longitude = -50.0
            myLocationNewOverlay.enableMyLocation()
            myLocationNewOverlay.onLocationChanged(location, null)
            finished = true
        }
        while (!finished)
            sleep(100)

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
        GoalPointManager.getGoalPointManager(context)
        GoalTimer.start()
        Thread.sleep(3000)
        assert(GoalTimer.getSecondPassed().value > 2.9 && GoalTimer.getSecondPassed().value < 3.1){"${GoalTimer.getSecondPassed().value} : 3"}
    }

    fun happy_CreateGoalPointManager(){
        var manager = GoalPointManager.getGoalPointManager(context)
        assert(manager.context != null)
        manager = GoalPointManager.getGoalPointManager(null)
        assert(manager.context != null)
    }

    fun unhappy_CreateGoalPointManager(){

        val exception = assertThrows(IllegalStateException::class.java) {
            GoalPointManager.getGoalPointManager(null)
        }
        assertEquals("needs context to initialise", exception.message)
    }

    @Test
    fun happy_GoalPointStartStop(){
        var manager = GoalPointManager.getGoalPointManager(context)
        manager.start(myLocationNewOverlay)
        assert(manager.amountOffGeofences == manager.totalPoints())
        assert(manager.totalPoints() > 0)
        assert(manager.hasStarted())
        assert(!manager.hasFinished())
        assert(manager.getGoals().isNotEmpty())

        manager.stop()
        assert(manager.amountOffGeofences == 0)
        assert(!manager.hasStarted())
        assert(!manager.hasFinished())
        assert(manager.goalsVisited.value == 0)
        assert(manager.getGoals().isEmpty())

    }

    @Test
    fun unhappy_GoalPointStartStop(){
        var manager = GoalPointManager.getGoalPointManager(context)
        val location: MyLocationNewOverlay? = null
        val exception = assertThrows(NullPointerException::class.java) {
            manager.start(location!!)
        }
        assertEquals(null, exception.message)
    }

    @Test
    fun happy_TotalPointsVisited(){
        var manager = GoalPointManager.getGoalPointManager(context)
        manager.start(myLocationNewOverlay)
        manager.getGoals()[0].visited.value = true
        val totalVisited = manager.totalPointsVisited()
        assert(totalVisited.value == 1)
    }

    @Test
    fun unhappy_TotalPointsVisited(){
        var manager = GoalPointManager.getGoalPointManager(context)
        manager.start(myLocationNewOverlay)
        manager.goalsVisited.value = 2
        val totalVisited = manager.totalPointsVisited()
        assert(manager.goalsVisited.value == totalVisited.value)
    }

    @Test
    fun happy_GenerateGoals(){
        var manager = GoalPointManager.getGoalPointManager(context)
        manager.generateGoals(myLocationNewOverlay)
        assert(manager.getGoals().isNotEmpty())
        manager.getGoals().forEach(){
            assert(it.location.latitude != myLocationNewOverlay.myLocation.latitude)
            assert(it.location.longitude != myLocationNewOverlay.myLocation.longitude)
            assert(!it.visited.value)
        }
    }

    @Test
    fun unhappy_GenerateGoals(){
        var manager = GoalPointManager.getGoalPointManager(context)
        val location: MyLocationNewOverlay? = null
        val exception = assertThrows(NullPointerException::class.java) {
            manager.generateGoals(location!!)
        }
        assertEquals(null, exception.message)
    }


}