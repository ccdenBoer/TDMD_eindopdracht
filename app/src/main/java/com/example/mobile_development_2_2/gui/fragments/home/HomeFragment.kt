package com.example.mobile_development_2_2.gui.fragments.home

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.mobile_development_2_2.R
import com.example.mobile_development_2_2.data.Lang
import com.example.mobile_development_2_2.map.route.POI
import androidx.compose.ui.text.TextStyle as TextStyle1

@Composable
fun HomeScreen(modifier: Modifier, helpItems: List<HelpItem>, onPOIButtonClicked: () -> Unit) {
    val configuration = LocalConfiguration.current

    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    Surface(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {
        LazyColumn {
            item {
                Header(screenHeight.div(8).value)
            }

            item {
                Content(helpItems = helpItems, onPOIButtonClicked)
            }
        }


    }

}

@Composable
private fun Header(height: Float) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height.dp)
            .background(MaterialTheme.colors.background)
    ) {
        Text(
            text = Lang.get(R.string.home_welcome_to_chlam), style = TextStyle1(
                fontSize = 30.sp,
                color = MaterialTheme.colors.primary,
            ), modifier = Modifier.align(Alignment.Center)

        )
    }
}

@Composable
private fun Content(helpItems: List<HelpItem>, onPOIButtonClicked: () -> Unit) {
    val configuration = LocalConfiguration.current

    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    LazyVerticalGrid(columns = GridCells.Adaptive(screenWidth.div(3)),
        modifier = Modifier
            .fillMaxWidth()
            .height(screenHeight.minus(screenHeight.div(5)))
            .background(MaterialTheme.colors.background),
        // content padding
        contentPadding = PaddingValues(
            start = 12.dp, top = 12.dp, end = 12.dp, bottom = 16.dp
        ),
        content = {
            items(helpItems.size) { index ->
                MessageRow(helpItems.get(index), onPOIButtonClicked)
            }

        })
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MessageRow(helpItem: HelpItem, onPOIButtonClicked: () -> Unit) {

    Card(
        onClick = {
            HelpItem.selectItem(helpItem)
            onPOIButtonClicked()
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .background(
               MaterialTheme.colors.background, RectangleShape
            )
            .padding(12.dp)
            .clip(RoundedCornerShape(12.dp)),
        elevation = 10.dp,
        backgroundColor = MaterialTheme.colors.surface
    ) {

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
            colorFilter = null
        )

        Text(
            text = helpItem.title,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(bottom = 12.dp)
                .wrapContentHeight(Alignment.Bottom)
        )
    }
}




