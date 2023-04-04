package com.example.mobile_development_2_2.gui.fragments.poi


import android.app.Application
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.mobile_development_2_2.map.route.POI
import com.example.mobile_development_2_2.map.route.RouteManager

@Composable
fun POIDetailScreen(modifier: Modifier, poi: POI) {
    Surface(
        modifier = modifier.fillMaxSize()
    ) {
        LazyColumn {
            item {
                MessageRow1(poi)
            }
            item {
                MessageRow2(poi)
            }
            item {
                MessageRow3(poi)
            }

        }


    }
}

@Composable
fun MessageRow1(poi: POI) {


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(370.dp)
            .background(
                MaterialTheme.colors.background, RectangleShape
            )
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
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                //.align(Alignment.Center)
                .padding(start = 24.dp, end = 24.dp, top = 12.dp, bottom = 80.dp)
                .clip(
                    RoundedCornerShape(24.dp)
                ),
            alignment = Alignment.Center,
            alpha = DefaultAlpha,
            colorFilter = null
        )

        Text(
            text = poi.name,
            textAlign = TextAlign.Center, // make text center horizontal
            modifier = Modifier
                .width(150.dp)
                .height(150.dp)
                .padding(36.dp)
                .wrapContentHeight(Alignment.Bottom),
            fontSize = 30.sp
        )


        Text(
            text = poi.streetName,
            textAlign = TextAlign.Center, // make text center horizontal
            modifier = Modifier
                .width(150.dp)
                .height(150.dp)
                .padding(4.dp)
                .wrapContentHeight(Alignment.Bottom) // align bottom
        )

    }


}

@Composable
fun MessageRow2(poi: POI) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(
                MaterialTheme.colors.background, RectangleShape
            )
            .padding(12.dp)
            .clip(RoundedCornerShape(12.dp)),
        elevation = 10.dp,
        backgroundColor = MaterialTheme.colors.surface
    )
    {

        TextField(
            value = RouteManager.getRouteManager(null).getStringByName(poi.longDescription),
            readOnly = true,
            onValueChange = { },
            label = { Text(text = "") },
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = MaterialTheme.colors.background
            )
        )

    }
}

@Composable
fun MessageRow3(poi: POI) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(
                MaterialTheme.colors.background, RectangleShape
            )
            .padding(12.dp)
            .clip(RoundedCornerShape(12.dp)),
        elevation = 10.dp,
        backgroundColor = MaterialTheme.colors.surface
    )
    {
        val application = LocalContext.current.applicationContext as Application
        val imageStream = application.assets.open(poi.imgMap)
        val imageDrawable = Drawable.createFromStream(imageStream, null)
        Image(
            bitmap = imageDrawable!!.toBitmap().asImageBitmap(),
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                //.align(Alignment.Center)
                .padding(12.dp)
                .clip(
                    RoundedCornerShape(12.dp)
                ),
            alignment = Alignment.Center,
            alpha = DefaultAlpha,
            colorFilter = null
        )
    }
}


