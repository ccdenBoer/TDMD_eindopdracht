package com.example.mobile_development_2_2.gui.fragments.route

import android.Manifest
import android.app.Application
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import com.example.mobile_development_2_2.R
import com.example.mobile_development_2_2.data.Lang
import com.example.mobile_development_2_2.map.route.Route
import com.example.mobile_development_2_2.map.route.RouteManager
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlin.math.round


@Composable
fun RouteListScreen(
    modifier: Modifier, routes: List<Route>, onRouteClicked: () -> Unit, onPOIClicked: () -> Unit
) {
    Surface(
        modifier = modifier.fillMaxSize()
    ) {
        LazyColumn {
            items(routes) { route ->
                MessageRow(route, onRouteClicked, onPOIClicked)
            }

        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MessageRow(route: Route, onRouteClicked: () -> Unit, onPOIClicked: () -> Unit) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(500.dp)
            .background(
              MaterialTheme.colors.background, RectangleShape
            )
            .padding(12.dp)
            .clip(RoundedCornerShape(12.dp)),
        elevation = 10.dp,
        backgroundColor = MaterialTheme.colors.surface
    ) {
        val application = LocalContext.current.applicationContext as Application
        Log.d("image", route.name)
        Log.d("image", route.img)
        val imageStream = application.assets.open(route.img)
        val imageDrawable = Drawable.createFromStream(imageStream, null)
        Image(
            bitmap = imageDrawable!!.toBitmap().asImageBitmap(),
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp, top = 8.dp, bottom = 150.dp)
                .clip(
                    RoundedCornerShape(12.dp)
                ),
            alignment = Alignment.Center,
            alpha = DefaultAlpha,
            colorFilter = null
        )

        Text(
            text = route.name,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(bottom = 110.dp, top = 24.dp)
                .wrapContentHeight(Alignment.Bottom),
            fontSize = 30.sp
        )

        Text(
            text = Lang.get(R.string.route_distance) + ": ${round(route.getTotalLength()*100)/100} km",
            textAlign = TextAlign.Start,
            modifier = Modifier
                .padding(bottom = 80.dp, top = 80.dp, start = 12.dp)
                .wrapContentHeight(Alignment.Bottom)
        )

        Text(
            text = Lang.get(R.string.routes_waypoints) + ": ${route.POIs.size}",
            textAlign = TextAlign.End,
            modifier = Modifier
                .padding(bottom = 80.dp, top = 80.dp, end = 12.dp)
                .wrapContentHeight(Alignment.Bottom)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(35.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Button(
                onClick = {
                    RouteManager.getRouteManager(application.baseContext).selectRoute(route)
                    onPOIClicked()
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colors.primary,
                    contentColor = MaterialTheme.colors.onPrimary,
                ),
                modifier = Modifier
                    .width(150.dp)
                    .height(35.dp)
                    .offset(-100.dp, -25.dp)
            ) {
                Text(text = Lang.get(R.string.routes_waypoints))
            }

            val premissions = rememberMultiplePermissionsState(
                listOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                )
            )
            Button(
                onClick = {
                    RouteManager.getRouteManager(application.baseContext).selectRoute(route)
                    Log.d("route", route.name)
                    premissions.launchMultiplePermissionRequest()
                    onRouteClicked()
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colors.primary, contentColor = MaterialTheme.colors.onPrimary
                ), modifier = Modifier
                    .width(150.dp)
                    .height(35.dp)
                    .offset(100.dp, (-25).dp)
            ) {
                Text(text =  Lang.get(R.string.routes_map))
            }
        }
    }


}



