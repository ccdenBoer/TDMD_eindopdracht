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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
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
import com.example.mobile_development_2_2.map.RouteRequest
import com.example.mobile_development_2_2.R
import com.example.mobile_development_2_2.data.Client
import com.example.mobile_development_2_2.data.Lang
import com.example.mobile_development_2_2.map.route.POI
import com.example.mobile_development_2_2.map.route.Route
import com.example.mobile_development_2_2.map.route.RouteManager
import com.example.mobile_development_2_2.ui.viewmodels.OSMViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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
    lateinit var route: Route
    lateinit var currentDestination: GeoPoint
    var lastLocation: GeoPoint = GeoPoint(0.0, 0.0)


    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    fun MapScreen(viewModel: OSMViewModel, modifier: Modifier, onPOIClicked: () -> Unit) {
        viewModel.provider.locationListener = this

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

            if (premissions.allPermissionsGranted) {


            }

            OSM(
                modifier = modifier,
                routePoints = viewModel.pois.map { it.location }.toMutableList(),
                provider = viewModel.provider,
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
            if (!RouteManager.getRouteManager(null).getSelectedRoute().started.value) {

                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = {
                            Log.d("f", "" + route.started.value)

                            RouteManager.getRouteManager(context).setRouteState(true);
                            currentDestination =
                                RouteManager.getRouteManager(context).getSelectedPOI().location
                            Log.d("f", "" + route.started.value)
                            route.started.value

                            var lat: Double = route.POIs[0].location.latitude
                            var lng: Double = route.POIs[0].location.longitude

                            RouteManager.getRouteManager(null).goToNextGeofence()

                        },
                        modifier = Modifier
                            .padding(bottom = 20.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colors.primary,
                            contentColor = Color.White
                        )
                    ) {
                        if (!route.hasProgress())
                            Text(
                                text = Lang.get(R.string.map_start),
                                color = MaterialTheme.colors.onPrimary
                            )
                        else
                            Text(
                                text = Lang.get(R.string.map_continue),
                                color = MaterialTheme.colors.onPrimary
                            )
                    }

                }
            }
            if (route.started.value) {
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

                Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.Start) {
                    Button(
                        onClick = {
                            RouteManager.getRouteManager(context).setRouteState(false)
                            RouteManager.getRouteManager(null).removeGeofence()
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

                Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.End) {
                    Card(
                        modifier = Modifier
                            .padding(top = 20.dp, end = 30.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .height(60.dp)
                            .width(120.dp),
                        elevation = 10.dp,
                        backgroundColor = MaterialTheme.colors.primary

                    ) {
                        Text(
                            text = "" + route.totalPoisVisited.value + " / " + route.POIs.size + " " + Lang.get(
                                R.string.map_points
                            ),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .wrapContentHeight(Alignment.Top)
                                .padding(top = 6.dp),
                            color = MaterialTheme.colors.onPrimary

                        )
                        Text(
                            text = "${round(route.currentLength.value * 100) / 100} / ${round(route.getTotalLength() * 100) / 100} km",
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
    }


    @Composable
    private fun OSM(

        modifier: Modifier = Modifier,
        routePoints: MutableList<GeoPoint> = mutableListOf(),
        provider: IMyLocationProvider,
        onPOIClicked: () -> Unit,
    ) {
        this.route = RouteManager.getRouteManager(null).getSelectedRoute()
        val locations = route.POIs
        val listener = object : ItemizedIconOverlay.OnItemGestureListener<POIItem> {
            override fun onItemSingleTapUp(index: Int, item: POIItem?): Boolean {
                if (item != null) {
                    clickedOnPoi(item.poi, onPOIClicked)
                }
                return true
            }

            override fun onItemLongPress(index: Int, item: POIItem?): Boolean {
                if (item != null) {
                    longClickOnPoi(item.poi, onPOIClicked)
                }
                return false
            }
        }
        context = LocalContext.current


        val mapView = remember {
            MapView(context)
        }
        this.mapView = mapView
        mapView.setMultiTouchControls(true)


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


        val currentRoute = remember {
            Polyline()
        }
        currentRoute.outlinePaint.color = MaterialTheme.colors.primary.toArgb()

        currentRoute.setPoints(routePoints)
        //todo Als we willen dat de gelopen route wordt getekend en/of een correctieroute wordt getekend
//        val walkedRoute = remember {
//            Polyline()
//        }
//        walkedRoute.color = R.color.black
//        val correctionRoute = remember {
//            Polyline()
//        }
//        correctionRoute.color = R.color.teal_700


        val myLocation = remember(mapView) {
            MyLocationNewOverlay(provider, mapView)
        }
        this.myLocation = myLocation
        myLocation.enableMyLocation()



        AndroidView(
            factory = {
                mapView.apply {
                    setTileSource(TileSourceFactory.MAPNIK)
                    isTilesScaledToDpi = true
                    controller.setCenter(GeoPoint(51.58703, 4.773187)) // Avans
                    controller.setZoom(17.0)


                    //mapView.overlays.add(poiOverlay)


                    mapView.overlays.add(poiOverlay)
                    mapView.overlays.add(visitedOverlay)
                    mapView.overlays.add(currentOverlay)

                    //mapView.overlays.add(myLocation)

//                    mapView.overlays.add(currentRoute)


                }
            },
            modifier = modifier,
            update = {
                if (route.started.value) {
                    myLocation.disableFollowLocation()
                    myLocation.enableFollowLocation()

                } else {
                    myLocation.disableFollowLocation()
                    mapView.mapOrientation = 0f

                }
            }

        )

        LaunchedEffect(locations) {
            poiOverlay.removeAllItems()
            poiOverlay.addItems(
                locations.filter { !it.visited }.filterIndexed { index, poi -> index != 0 }
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
                locations.filter { !it.visited }.filterIndexed { index, poi -> index == 0 }
                    .map { POIItem(it) }
            )
            mapView.invalidate() // Ensures the map is updated on screen
        }


    }

    fun setRoute(start: GeoPoint, end: GeoPoint) {


        var route: String

        val resources = mapView.resources


//        val inputStream = resources.openRawResource(R.raw.test_route)

//        val inputStream = resources.openRawResource(R.raw.test_route)

        val kmldocument = KmlDocument()
//        kmldocument.parseGeoJSONStream(inputStream)
        runBlocking {
            GlobalScope.launch {
//               val client = Client("192.168.5.1",8000)
//               client.sendGeoLocation(GeoPoint(start.latitude,start.longitude))
                Log.d(LOG_TAG, "requesting route")
                val klmstyle = Style(
                    null, Color.Red.hashCode(), 20f, Color.White.hashCode()
                )

                route = RouteRequest.getRoute(start, end, null)

                kmldocument.parseGeoJSON(route)
                if (feature != null) {
                    mapView.overlays.remove(feature)

                }
                lastLocation = start

                feature = kmldocument.mKmlRoot.buildOverlay(
                    mapView,
                    klmstyle,
                    null,
                    kmldocument
                ) as FolderOverlay;
                var count = 0
                println("(feature.items.size) heuu")
                feature.items.forEach {
                    if (it is Polyline) {
                        it.outlinePaint.color =
                            ColorUtils.HSLToColor(floatArrayOf(count.toFloat(), 1f, 0.5f))

                        it.outlinePaint.strokeWidth = 20f
                        count += 10
                    }
                }
                mapView.overlays.add(feature)
                mapView.overlays.add(myLocation)
                mapView.invalidate()


//                if (mapView.overlays.contains(feature)){
//                    mapView.overlays.remove(feature)
//                    mapView.invalidate()
//                    mapView.overlays.add(feature)
//
//                }else{
//                    mapView.overlays.add(feature)
//                }


            }
        }

//        val kmlIcon = BitmapDrawable(resources, BitmapFactory.decodeResource(resources, R.drawable.marker_node))


    }

    private fun clickedOnPoi(poi: POI, onPOIClicked: () -> Unit) {
        RouteManager.getRouteManager(context).selectPOI(poi)
        onPOIClicked()
    }

    private fun longClickOnPoi(poi: POI, onPOIClicked: () -> Unit) {
        //todo
    }

    override fun onLocationChanged(p0: Location) {


        if (myLocation.isFollowLocationEnabled) {
            route.updateLength()


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
        //myLocation.enableFollowLocation()
        if (System.currentTimeMillis() - lastrouterequest > 1800) {

            lastrouterequest = System.currentTimeMillis()
            if (::currentDestination.isInitialized) {
                if (currentDestination != RouteManager.getRouteManager(context)
                        .get_CurrentPoi().location
                ) {
                    currentDestination =
                        RouteManager.getRouteManager(context).get_CurrentPoi().location
//                  setRoute(myLocation.myLocation, currentDestination)
                }

                setRoute(GeoPoint(p0.latitude, p0.longitude), currentDestination)

            }
        }
        mapView.invalidate()

    }
}


private class POIItem(
    val poi: POI //FIXME add poiClass,
) : OverlayItem(poi.name, null, GeoPoint(poi.location.latitude, poi.location.longitude))
