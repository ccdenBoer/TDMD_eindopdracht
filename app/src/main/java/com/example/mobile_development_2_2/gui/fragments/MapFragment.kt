package com.example.mobile_development_2_2.gui.fragments

import android.Manifest
import android.content.Context
import android.location.Location
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*

import androidx.compose.runtime.*

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.example.mobile_development_2_2.R
import com.example.mobile_development_2_2.data.GoalDatabase
import com.example.mobile_development_2_2.data.GoalTimer
import com.example.mobile_development_2_2.gui.GoalPoint
import com.example.mobile_development_2_2.gui.GoalPointManager
import com.example.mobile_development_2_2.ui.viewmodels.OSMViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.*
import kotlinx.coroutines.flow.*
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.FolderOverlay
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.OverlayItem
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.util.*
import kotlin.math.round


class MapFragment : LocationListener {

    val LOG_TAG = "MapFragment"
    lateinit var myLocation: MyLocationNewOverlay
    lateinit var mapView: MapView
    lateinit var context: Context
    var lastrouterequest = System.currentTimeMillis()
    var feature = FolderOverlay()
    lateinit var currentDestination: GeoPoint
    var lastLocation: GeoPoint = GeoPoint(0.0, 0.0)


    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    fun MapScreen(viewModel: OSMViewModel, modifier: Modifier, onPOIClicked: () -> Unit, database: GoalDatabase) {
        context = LocalContext.current
        viewModel.provider.locationListener = this
        val mapView = remember {
            MapView(context)
        }
        this.mapView = mapView
        mapView.setMultiTouchControls(true)

        myLocation = remember(mapView) {
            MyLocationNewOverlay(viewModel.provider, mapView)
        }
        myLocation.enableMyLocation()


        Surface(
            modifier = modifier.fillMaxSize()
        ) {
            val premissions = rememberMultiplePermissionsState(
                listOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                )
            )


            OSM(
                modifier = modifier,
                provider = viewModel.provider,
                routePoints = GoalPointManager.getGoalPointManager(context).getGoalsAsGeoPoint()
                    .toMutableList(),
                onPOIClicked = onPOIClicked
            )




            if (!premissions.allPermissionsGranted) {
                Column {

                    Text(
                        text = context.resources.getString(R.string.map_no_location_permission),
                        color = MaterialTheme.colors.error
                    )
                }
            }
            Row {
                Text(
                    text = context.resources.getString(R.string.map_copyright),
                    fontSize = 8.sp,
                    modifier = Modifier
                        .background(MaterialTheme.colors.surface, RectangleShape)
                        .align(Alignment.Bottom)
                )
            }

            Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.End) {
                Button(
                    onClick = { myLocation.enableFollowLocation() },
                    modifier = Modifier
                        .padding(bottom = 20.dp, end = 30.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.primary
                    )
                ) {
                    Text(
                        text = context.resources.getString(R.string.map_recenter),
                        color = MaterialTheme.colors.onPrimary
                    )
                }
            }

            if (!GoalPointManager.getGoalPointManager(null).hasStarted()) {

                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = {
                            try{
                                GoalPointManager.getGoalPointManager(null).start(myLocation)
                                GoalTimer.start()

                            } catch (e:Exception){
                                Log.d(LOG_TAG, "No gps data yet")
                            }

                        },
                        modifier = Modifier
                            .padding(bottom = 20.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colors.primary,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = context.resources.getString(R.string.map_start),
                            color = MaterialTheme.colors.onPrimary
                        )

                    }
                }

            }
        }
        if (GoalPointManager.getGoalPointManager(null).hasStarted()) {

            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.Start
            ) {
                Button(
                    onClick = {
                        GoalPointManager.getGoalPointManager(null).stop();
                    },
                    modifier = Modifier
                        .padding(top = 20.dp, start = 30.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.primary,
                        contentColor = MaterialTheme.colors.onPrimary
                    )

                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_close_24),
                        contentDescription = ""
                    )
                }

            }

            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.End
            ) {
                Card(
                    modifier = Modifier
                        .padding(top = 20.dp, end = 30.dp, start = 250.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .height(60.dp)
                        .width(120.dp),
                    elevation = 10.dp,
                    backgroundColor = MaterialTheme.colors.primary

                ) {
                    Text(
                        text = "${GoalPointManager.getGoalPointManager(null).goalsVisited.value} / ${GoalPointManager.getGoalPointManager(null).totalPoints()} " + context.resources.getString(
                            R.string.map_points
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .wrapContentHeight(Alignment.Top)
                            .padding(top = 6.dp),
                        color = MaterialTheme.colors.onPrimary

                    )
                    Text(
                        text = "${round(GoalTimer.secondsPassed.value * 10) / 10} sec",
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .wrapContentHeight(Alignment.Bottom)
                            .padding(bottom = 8.dp),
                        color = MaterialTheme.colors.onPrimary
                    )
                }

            }

        }
    }


    @Composable
    private fun OSM(

        modifier: Modifier = Modifier,
        provider: IMyLocationProvider,
        routePoints: MutableList<GeoPoint> = mutableListOf(),
        onPOIClicked: () -> Unit,
    ) {

        val locations = GoalPointManager.getGoalPointManager(null).getGoals()
        val listener = object : ItemizedIconOverlay.OnItemGestureListener<GoalItem> {
            override fun onItemSingleTapUp(index: Int, item: GoalItem?): Boolean {

                return true
            }

            override fun onItemLongPress(index: Int, item: GoalItem?): Boolean {

                return false
            }
        }




        //FIXME poilayer kan toegevoegd worden als er point of interest zijn
        val unvisitedGoalOverlay = remember {
            ItemizedIconOverlay(
                mutableListOf<GoalItem>(),
                ContextCompat.getDrawable(
                    context,
                    R.drawable.red_point
                ),
                listener,
                context
            )
        }
        val visitedGoalOverlay = remember {
            ItemizedIconOverlay(
                mutableListOf<GoalItem>(),
                ContextCompat.getDrawable(
                    context,
                    R.drawable.green_point
                ),
                listener,
                context
            )
        }






        AndroidView(
            factory = {
                mapView.apply {
                    setTileSource(TileSourceFactory.MAPNIK)
                    isTilesScaledToDpi = true
                    if(provider.lastKnownLocation == null){
                        controller.setCenter(GeoPoint(51.58703, 4.773187)) // Avans
                    } else{
                        controller.setCenter(GeoPoint(provider.lastKnownLocation.latitude, provider.lastKnownLocation.longitude))
                    }

                    controller.setZoom(17.0)
                    mapView.overlays.add(myLocation)


                    mapView.overlays.add(unvisitedGoalOverlay)
                    mapView.overlays.add(visitedGoalOverlay)
                }
            },
            modifier = modifier,
            update = {
                myLocation.enableFollowLocation()
            }

        )

        LaunchedEffect(key1 = GoalPointManager.getGoalPointManager(null).getGoals()) {
            unvisitedGoalOverlay.removeAllItems()
            unvisitedGoalOverlay.addItems(
                locations.filter { !it.visited.value }
                    .map { GoalItem(it) }
            )

            mapView.invalidate() // Ensures the map is updated on screen
        }
        LaunchedEffect(key1 = GoalPointManager.getGoalPointManager(null).getGoals()) {
            visitedGoalOverlay.removeAllItems()
            visitedGoalOverlay.addItems(
                locations.filter { it.visited.value }.map { GoalItem(it) }
            )
            mapView.invalidate() // Ensures the map is updated on screen
        }
        GoalPointManager.getGoalPointManager(null).getGoals().toMutableList().forEach(){
            rememberUpdatedState(it.visited.value)
        }
        var forceRedraw by remember { mutableStateOf(false) }
        locations.forEach { location ->
            LaunchedEffect(location.visited.value) {
                // Update the overlay for the current location
                forceRedraw = !forceRedraw
                visitedGoalOverlay.removeAllItems()
                visitedGoalOverlay.addItems(
                    locations.filter { it.visited.value }.map { GoalItem(it) }
                )
                unvisitedGoalOverlay.removeAllItems()
                unvisitedGoalOverlay.addItems(
                    locations.filter { !it.visited.value }
                        .map { GoalItem(it) }
                )
                mapView.invalidate() // Ensures the map is updated on screen
            }
        }
        rememberUpdatedState(forceRedraw)
        




    }

    override fun onLocationChanged(p0: Location) {


        mapView.mapOrientation = 0f
        mapView.setMapCenterOffset(0, 0)
        myLocation.isDrawAccuracyEnabled = true
        myLocation.setDirectionIcon(
            ContextCompat.getDrawable(
                context,
                R.drawable.redpointer
            )!!.toBitmap(100, 100)
        )

        mapView.invalidate()

    }
}

private class GoalItem(
    val gp: GoalPoint
) : OverlayItem("hallo", null, GeoPoint(gp.location.latitude, gp.location.longitude))
