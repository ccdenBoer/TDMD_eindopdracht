package com.example.mobile_development_2_2.gui.fragments.home

import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.mobile_development_2_2.R

@Composable
fun InfoScreen(modifier: Modifier, helpItem: HelpItem) {


    Surface(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.surface)
    ) {
        Content(helpItem = helpItem)


    }

}

@Composable
private fun Content(helpItem: HelpItem) {
    val configuration = LocalConfiguration.current

    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background),
        // content padding
        contentPadding = PaddingValues(
            start = 12.dp,
            top = 12.dp,
            end = 12.dp,
            bottom = 16.dp
        ),
        content = {
            item() {
                MessageRow1(helpItem)
            }

            item() {
                MessageRow2(helpItem)
            }

        })
}

@Composable
fun MessageRow1(helpItem: HelpItem) {
    val configuration = LocalConfiguration.current

    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(screenHeight.div(2))
            .background(
                MaterialTheme.colors.background, RectangleShape
            )
            .padding(12.dp)
            .clip(RoundedCornerShape(12.dp)),
        elevation = 10.dp,
        backgroundColor = MaterialTheme.colors.background
    )
    {

        Image(
            painter = painterResource(id = helpItem.imgId),
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .padding(24.dp)
                .clip(
                    RoundedCornerShape(12.dp)
                ),
            alignment = Alignment.CenterStart,
            alpha = DefaultAlpha,
            colorFilter = null,

        )
    }
}

@Composable
fun MessageRow2(helpItem: HelpItem) {
    val configuration = LocalConfiguration.current

    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    Card(
        modifier = Modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colors.background, RectangleShape
            )
            .padding(12.dp)
            .clip(RoundedCornerShape(12.dp)),
        elevation = 10.dp,
        backgroundColor = Color.White
    )
    {

        TextField(
            value = helpItem.description,
            readOnly = true,
            onValueChange = { },
            label = { Text(text = "") },
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = MaterialTheme.colors.background
            )
        )
    }


}


