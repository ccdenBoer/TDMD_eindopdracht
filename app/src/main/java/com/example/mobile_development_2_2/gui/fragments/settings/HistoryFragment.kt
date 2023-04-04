package com.example.mobile_development_2_2.gui.fragments.settings

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
import androidx.compose.ui.text.style.TextDirection.Companion.Content
import com.example.mobile_development_2_2.R
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.example.mobile_development_2_2.data.GoalDatabase
import com.example.mobile_development_2_2.data.GoalTimer
import com.example.mobile_development_2_2.data.Lang
import com.example.mobile_development_2_2.data.loadWinsFromDatabase
import kotlin.math.round

@Composable
fun SettingsFragment(modifier: Modifier, database: GoalDatabase) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxSize()
    ) {
        Card(
            modifier = Modifier.padding(bottom = 50.dp),
            backgroundColor = MaterialTheme.colors.surface
        ) {
            Settings()
        }
        Card(
            backgroundColor = MaterialTheme.colors.surface,
            elevation = 0.dp,
        ) {
            HistoryList(database = database)
        }
        Card(
            modifier = Modifier.padding(top = 50.dp),
            backgroundColor = MaterialTheme.colors.surface
        ) {
            Copyright()
        }
    }
}


@Composable
fun Settings() {
    var expanded by remember { mutableStateOf(false) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = Lang.get(R.string.settings_language),
                modifier = Modifier.padding(8.dp),
            )
            Row(
                Modifier.clickable { expanded = !expanded },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = Lang.language.first)
                Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = null)
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    properties = PopupProperties(focusable = false)
                ) {
                    Lang.languages.forEach { l ->
                        DropdownMenuItem(onClick = {
                            expanded = false
                            Lang.setLang(l)
                        }) {
                            Text(text = l.first)
                        }
                    }
                }
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = Lang.get(R.string.settings_colour_blind),
                modifier = Modifier.padding(8.dp),
            )
            Switch(
                checked = Lang.colorblind,
                onCheckedChange = {
                    Lang.setColor(it);
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colors.primary,
                    uncheckedThumbColor = MaterialTheme.colors.primary,
                    checkedTrackColor = MaterialTheme.colors.onPrimary,
                    uncheckedTrackColor = MaterialTheme.colors.primary,
                )
            )
        }
    }
}
var finished: MutableState<Boolean> = mutableStateOf(false)
var wins: MutableList<GoalDatabase.Win> = mutableListOf()
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
                    backgroundColor = MaterialTheme.colors.surface
                ) {

                    Text(
                        text = "No History!!",
                        modifier = Modifier
                            .padding(start = 24.dp, bottom = 24.dp, top = 24.dp)
                            .offset(x = 190.dp)
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
        text = Lang.get(R.string.settings_copyright),
        modifier = Modifier.padding(8.dp),
    )
}