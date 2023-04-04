package com.example.mobile_development_2_2.gui.fragments.settings

import android.os.Bundle
import android.widget.ToggleButton
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.preference.PreferenceFragmentCompat
import com.example.mobile_development_2_2.R
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.example.mobile_development_2_2.data.Lang
import com.example.mobile_development_2_2.gui.fragments.home.HelpItem
    @Composable
    fun SettingsFragment(modifier: Modifier) {

        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = modifier.fillMaxSize()) {
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
                    AgsLogo()
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
        var expanded by remember { mutableStateOf(false)}

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                Text(
                    text = Lang.get(R.string.settings_language),
                    modifier = Modifier.padding(8.dp),
                )
                Row(Modifier.clickable { expanded = !expanded }, verticalAlignment = Alignment.CenterVertically) {
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
                        Lang.setColor(it); },
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

    @Composable
    fun AgsLogo() {
        Image(
            painter = painterResource(id = R.drawable.ic_logo),
            contentDescription = null,
            modifier = Modifier.background(color = MaterialTheme.colors.background),
                //.align(Alignment.CenterStart)
            alignment = Alignment.Center,
            alpha = DefaultAlpha,
            colorFilter = null)
    }

    @Composable
    fun Copyright() {
        Text(
            text = Lang.get(R.string.settings_copyright),
            modifier = Modifier.padding(8.dp),
        )
    }