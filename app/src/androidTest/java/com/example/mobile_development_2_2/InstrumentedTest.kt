package com.example.mobile_development_2_2

import android.content.Context
import android.location.Location
import android.os.Handler
import android.os.Looper
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.mobile_development_2_2.data.*
import com.example.mobile_development_2_2.gui.GoalPointManager
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import org.junit.After

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.lang.Thread.sleep
import java.lang.reflect.Field

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class InstrumentedTest {
    private lateinit var geofenceHelper: GeofenceHelper
    private lateinit var context: Context
    private lateinit var geofencingClient : GeofencingClient

    private lateinit var mapView: MapView
    private lateinit var myLocationNewOverlay: MyLocationNewOverlay
    private lateinit var location: Location

    @Before
    fun before() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        geofenceHelper = GeofenceHelper(context)
        geofencingClient = LocationServices.getGeofencingClient(context)
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
    @After
    fun resetGoalPointManager() {
        val field: Field = GoalPointManager::class.java.getDeclaredField("goalPointManager")
        field.isAccessible = true
        field.set(null, null)
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
        GoalPointManager.getGoalPointManager(context)
        GoalTimer.start()
        Thread.sleep(3000)
        assert(GoalTimer.started.value)
        assert(GoalTimer.getSecondPassed().value > 2.9 && GoalTimer.getSecondPassed().value < 3.1){"${GoalTimer.getSecondPassed().value} : 3"}
        assert(GoalTimer.started.value) {"${GoalTimer.started.value} : true"}
        GoalTimer.myTimer.cancel()
        GoalTimer.timerThread.interrupt()
        GoalTimer.started.value = false
    }

    @Test
    fun unhappy_timer() {
        GoalPointManager.getGoalPointManager(context)
        GoalTimer.start()
        Thread.sleep(2000)
        GoalTimer.start()
        Thread.sleep(1000)
        assert(GoalTimer.getSecondPassed().value > 0.9 && GoalTimer.getSecondPassed().value < 1.1){"${GoalTimer.getSecondPassed().value} : 1"}
        GoalTimer.myTimer.cancel()
        GoalTimer.timerThread.interrupt()
        GoalTimer.started.value = false
    }
    @Test
    fun happy_CreateGoalPointManager(){
        var manager = GoalPointManager.getGoalPointManager(context)
        assert(manager.context != null)
        manager = GoalPointManager.getGoalPointManager(null)
        assert(manager.context != null)
    }

    @Test
    fun unhappy_CreateGoalPointManager(){

        val exception = assertThrows(IllegalStateException::class.java) {
            GoalPointManager.getGoalPointManager(null)
        }
        assertEquals("Context must be provided when goalPointManager is null", exception.message)
    }

    @Test
    fun happy_addRemoveGeofence() {
        GoalPointManager.getGoalPointManager(context).addGeofenceLocation(0.0, 0.0, "test")
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
        GoalPointManager.getGoalPointManager(context).addGeofenceLocation(0.0, 0.0, "test")
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

    @Test
    fun happy_testDatabase(){
        val db = GoalDatabase.getInstance(context, true)
        var finished = false
        loadWinsFromDatabase(db){
            assert(it.size == 0) {"${it.size}"}
            finished = true
        }
        while(!finished){
            sleep(100)
        }
        finished = false
        getTotalWinsFromDatabase(db){
            assert(it == 0)
            finished = true
        }
        while(!finished){
            sleep(100)
        }
        finished = false
        val win = GoalDatabase.Win(0, "date", 15.4)
        addWinsFromDatabase(db, win){
            finished = true
        }
        while(!finished){
            sleep(100)
        }
        finished = false
        getTotalWinsFromDatabase(db){
            assert(it == 1)
            finished = true
        }
        while(!finished){
            sleep(100)
        }
        finished = false
        loadWinsFromDatabase(db){
            assert(it.isNotEmpty())
            assert(it[0].date == "date")
            assert(it[0].id == 0)
            assert(it[0].time == 15.4)
            finished = true
        }
        while(!finished){
            sleep(100)
        }
        finished = false
        clearDatabase(db){
            finished = true
        }
        while(!finished){
            sleep(100)
        }
        finished = false
        loadWinsFromDatabase(db){
            assert(it.isEmpty())
            finished = true
        }
        while(!finished){
            sleep(100)
        }
        finished = false
        getTotalWinsFromDatabase(db){
            assert(it == 0)
            finished = true
        }
        while(!finished){
            sleep(100)
        }

    }

    @Test
    fun unhappy_testDatabase(){
        val db = GoalDatabase.getInstance(context, true)
        var finished = false
        val invalidWin = GoalDatabase.Win(-1, "don matter", -1.0)
        addWinsFromDatabase(db, invalidWin){
            finished = true
        }

        while(!finished){
            sleep(100)
        }
        finished = false
        getTotalWinsFromDatabase(db){
            assert(it == 0)
            finished = true
        }
        while(!finished){
            sleep(100)
        }

        finished = false
        clearDatabase(db){
            finished = true
        }
        while(!finished){
            sleep(100)
        }
    }



}