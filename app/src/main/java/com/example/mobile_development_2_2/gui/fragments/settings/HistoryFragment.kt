package com.example.mobile_development_2_2.gui.fragments.settings

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDirection.Companion.Content
import com.example.mobile_development_2_2.R
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.example.mobile_development_2_2.data.GoalDatabase
import com.example.mobile_development_2_2.data.GoalTimer
import com.example.mobile_development_2_2.data.loadWinsFromDatabase
import kotlin.math.round

var finished: MutableState<Boolean> = mutableStateOf(false)
var wins: MutableList<GoalDatabase.Win> = mutableListOf()
lateinit var context: Context

@Composable
fun SettingsFragment(modifier: Modifier, database: GoalDatabase) {
    finished.value = false
    context = LocalContext.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxSize()
    ) {
        Card(
            modifier = Modifier.padding(top = 10.dp, bottom =  10.dp),
            backgroundColor = MaterialTheme.colors.background
        ) {
            Copyright()
        }
        Card(
            backgroundColor = MaterialTheme.colors.surface,
            elevation = 0.dp,
        ) {
            HistoryList(database = database)
        }

    }
}

@Composable
fun HistoryList(database: GoalDatabase) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {


        Log.d("HistoryFragment", "Loading list")
        loadWinsFromDatabase(database) { loadedWins ->
            Log.d("HistoryFragment", "Loaded list callback")
            wins.clear()
            wins.addAll(0, loadedWins)
            finished.value = true
        }

        if (finished.value) {
            Log.d("HistoryFragment", "Loaded win list")

            if (wins.isNotEmpty()) {
                LazyColumn {
                    items(wins.size) { id ->
                        MessageRow(wins[id])
                    }
                }
            } else {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(12.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    elevation = 10.dp,
                    backgroundColor = MaterialTheme.colors.background
                ) {

                    Text(
                        text = "No History!!",
                        modifier = Modifier
                            .padding(start = 24.dp, bottom = 24.dp, top = 24.dp)
                    )
                }
            }
        }
    }

}

@Composable
fun MessageRow(win: GoalDatabase.Win) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(12.dp)
            .clip(RoundedCornerShape(12.dp)),
        elevation = 10.dp,
        backgroundColor = MaterialTheme.colors.surface
    )
    {

        Text(
            text = "Won at: " + win.date,
            modifier = Modifier
                .padding(start = 24.dp, bottom = 24.dp, top = 24.dp)
        )

        Text(
            text = "Won in: ${(round(((win.time - win.time%60)/60)))} min, ${((round(win.time * 10)) / 10)%60} sec",
            modifier = Modifier
                .padding(start = 24.dp, bottom = 24.dp, top = 48.dp)
        )

    }
}

@Composable
fun Copyright() {
    Text(
        text = context.resources.getString(R.string.settings_copyright),
        modifier = Modifier.padding(8.dp),
    )
}