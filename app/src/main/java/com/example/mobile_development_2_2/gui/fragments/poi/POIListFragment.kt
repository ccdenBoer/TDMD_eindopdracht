@file:OptIn(ExperimentalMaterialApi::class)

package com.example.mobile_development_2_2.gui.fragments.poi


import android.app.Application
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.example.mobile_development_2_2.map.route.POI
import com.example.mobile_development_2_2.map.route.Route
import com.example.mobile_development_2_2.map.route.RouteManager


@Composable
fun POIListScreen(modifier: Modifier, route: Route, onPOIClicked: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colors.background,
        contentColor = MaterialTheme.colors.background


    ) {
        LazyColumn() {
            items(route.POIs) { poi ->
                MessageRow(poi, onPOIClicked)
            }

        }


    }

}

@Composable
fun MessageRow(poi: POI, onPOIClicked: () -> Unit) {

    Card(
        onClick = {
            RouteManager.getRouteManager(null).selectPOI(poi)
            onPOIClicked()
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(12.dp)
            .clip(RoundedCornerShape(12.dp)),
        elevation = 10.dp,
        backgroundColor = MaterialTheme.colors.surface
    )
    {
        val application = LocalContext.current.applicationContext as Application
        val imageStream = application.assets.open(poi.img)
        val imageDrawable = Drawable.createFromStream(imageStream, null)
        Image(
            bitmap = imageDrawable!!.toBitmap().asImageBitmap(),
            contentDescription = null,
            contentScale = ContentScale.FillHeight,
            modifier = Modifier
                .padding(24.dp)
                .clip(
                    RoundedCornerShape(12.dp)
                ),
            alignment = Alignment.CenterStart,
            alpha = DefaultAlpha,
            colorFilter = null
        )

        Text(
            text = poi.name,
            modifier = Modifier
                .padding(start = 24.dp, bottom = 24.dp, top = 24.dp)
                .offset(x = 190.dp)
        )

        Text(
            text = poi.streetName,
            modifier = Modifier
                .padding(start = 24.dp, bottom = 24.dp, top = 48.dp)
                .offset(x = 190.dp)
        )

        TextField(
            value = RouteManager.getRouteManager(null).getStringByName(poi.shortDescription),
            modifier = Modifier
                .padding(start = 200.dp, bottom = 24.dp, top = 65.dp, end = 24.dp)
                .width(50.dp),
            readOnly = true,
            onValueChange = { },
            label = { Text(text = "") },
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = MaterialTheme.colors.surface
            )
        )

    }
}






