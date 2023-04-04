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
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.drawable.toBitmap
import com.example.mobile_development_2_2.R
import com.example.mobile_development_2_2.data.Lang
import com.example.mobile_development_2_2.gui.GoalPoint
import com.example.mobile_development_2_2.gui.GoalPointManager
import com.example.mobile_development_2_2.ui.viewmodels.OSMViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.internal.wait
import org.osmdroid.bonuspack.kml.KmlDocument
import org.osmdroid.bonuspack.kml.KmlGeometry
import org.osmdroid.bonuspack.kml.Style
import org.osmdroid.api.IMapController
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.FolderOverlay
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.Overlay
import org.osmdroid.views.overlay.OverlayItem
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
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
    fun MapScreen(viewModel: OSMViewModel, modifier: Modifier, onPOIClicked: () -> Unit) {
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


            //GoalPointManager.getGoalPointManager().generateGoals(myLocation);


            OSM(
                modifier = modifier,
                provider = viewModel.provider,
                routePoints = GoalPointManager.getGoalPointManager().getGoalsAsGeoPoint()
                    .toMutableList(),
                onPOIClicked = onPOIClicked
            )




            if (!premissions.allPermissionsGranted) {
                Column {

                    Text(
                        text = Lang.get(R.string.map_no_location_permission),
                        color = MaterialTheme.colors.error
                    )
                }
            }
            Row {
                Text(
                    text = Lang.get(R.string.map_copyright),
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
                        text = Lang.get(R.string.map_recenter),
                        color = MaterialTheme.colors.onPrimary
                    )
                }
            }

            if (!GoalPointManager.getGoalPointManager().hasStarted()) {

                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = {

                            GoalPointManager.getGoalPointManager().start(myLocation);
                            //currentRoute.setPoints(GoalPointManager.getGoalPointManager().getGoalsAsGeoPoint() .toMutableList())
                        },
                        modifier = Modifier
                            .padding(bottom = 20.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colors.primary,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = Lang.get(R.string.map_start),
                            color = MaterialTheme.colors.onPrimary
                        )

                    }
                }

            }
        }
        if (GoalPointManager.getGoalPointManager().hasStarted()) {

            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.Start
            ) {
                Button(
                    onClick = {
                        GoalPointManager.getGoalPointManager().stop();
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
                        text = "unkown " + Lang.get(
                            R.string.map_points
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .wrapContentHeight(Alignment.Top)
                            .padding(top = 6.dp),
                        color = MaterialTheme.colors.onPrimary

                    )
                    Text(
                        text = "unkown",
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

        val locations = GoalPointManager.getGoalPointManager().getGoals();
        val listener = object : ItemizedIconOverlay.OnItemGestureListener<POIItem> {
            override fun onItemSingleTapUp(index: Int, item: POIItem?): Boolean {

                return true
            }

            override fun onItemLongPress(index: Int, item: POIItem?): Boolean {

                return false
            }
        }


        val currentRoute = remember {
            Polyline()
        }
        currentRoute.outlinePaint.color = MaterialTheme.colors.primary.toArgb()

        //FIXME poilayer kan toegevoegd worden als er point of interest zijn
        val poiOverlay = remember {
            ItemizedIconOverlay(
                mutableListOf<POIItem>(),
                ContextCompat.getDrawable(
                    context,
                    R.drawable.red_point
                ),
                listener,
                context
            )
        }
        val visitedOverlay = remember {
            ItemizedIconOverlay(
                mutableListOf<POIItem>(),
                ContextCompat.getDrawable(
                    context,
                    R.drawable.green_point
                ),
                listener,
                context
            )
        }
        val currentOverlay = remember {
            ItemizedIconOverlay(
                mutableListOf<POIItem>(),
                ContextCompat.getDrawable(
                    context,
                    R.drawable.blue_point
                ),
                listener,
                context
            )
        }


        //todo Als we willen dat de gelopen route wordt getekend en/of een correctieroute wordt getekend
//        val walkedRoute = remember {
//            Polyline()
//        }
//        walkedRoute.color = R.color.black
//        val correctionRoute = remember {
//            Polyline()
//        }
//        correctionRoute.color = R.color.teal_700


        AndroidView(
            factory = {
                mapView.apply {
                    setTileSource(TileSourceFactory.MAPNIK)
                    isTilesScaledToDpi = true
                    controller.setCenter(GeoPoint(51.58703, 4.773187)) // Avans
                    controller.setZoom(17.0)


                    mapView.overlays.add(poiOverlay)


                    mapView.overlays.add(poiOverlay)
                    mapView.overlays.add(visitedOverlay)
                    mapView.overlays.add(currentOverlay)

                    mapView.overlays.add(myLocation)

                    mapView.overlays.add(currentRoute)


                }
            },
            modifier = modifier,
            update = {
                myLocation.enableFollowLocation()
            }

        )

        LaunchedEffect(locations) {
            poiOverlay.removeAllItems()
            poiOverlay.addItems(
                locations.filter { !it.visited }.filterIndexed { index, gp -> index != 0 }
                    .map { POIItem(it) }
            )

            mapView.invalidate() // Ensures the map is updated on screen
        }
        LaunchedEffect(locations) {
            visitedOverlay.removeAllItems()
            visitedOverlay.addItems(
                locations.filter { it.visited }.map { POIItem(it) }
            )
            mapView.invalidate() // Ensures the map is updated on screen
        }
        LaunchedEffect(locations) {
            currentOverlay.removeAllItems()
            currentOverlay.addItems(
                locations.filter { !it.visited }.filterIndexed { index, gp -> index == 0 }
                    .map { POIItem(it) }
            )
            mapView.invalidate() // Ensures the map is updated on screen
        }


    }

    override fun onLocationChanged(p0: Location) {


        if (myLocation.isFollowLocationEnabled) {


            mapView.mapOrientation = 360 - p0.bearing


            mapView.controller.setZoom(17.0)
            mapView.setMapCenterOffset(0, 600)

            myLocation.setDirectionIcon(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.redpointer
                )!!.toBitmap(150, 150)
            )

        } else {
            mapView.mapOrientation = 0f
            mapView.setMapCenterOffset(0, 0)
            myLocation.isDrawAccuracyEnabled = true
            myLocation.setDirectionIcon(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.redpointer
                )!!.toBitmap(100, 100)
            )
        }

        mapView.invalidate()

    }
}

private class POIItem(
    val gp: GoalPoint
) : OverlayItem("hallo", null, GeoPoint(gp.location.latitude, gp.location.longitude))
