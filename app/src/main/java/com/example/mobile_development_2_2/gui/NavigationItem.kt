package com.example.mobile_development_2_2.gui

import com.example.mobile_development_2_2.R
import com.example.mobile_development_2_2.data.Lang

sealed class NavigationItem(var route: String, var icon: Int, var title: String){
    object Home : NavigationItem("home", R.drawable.ic_home, Lang.get(R.string.homeScreen))
    object Map : NavigationItem("map", R.drawable.ic_map, Lang.get(R.string.mapScreen))
    object POIs : NavigationItem("POIs", R.drawable.ic_poi, Lang.get(R.string.poiListScreen))
    object Settings : NavigationItem("settings", R.drawable.ic_settings, "Settings")
}
