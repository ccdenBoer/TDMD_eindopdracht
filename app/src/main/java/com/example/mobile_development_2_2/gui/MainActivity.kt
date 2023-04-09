package com.example.mobile_development_2_2.gui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
//import androidx.room.Room
import com.example.mobile_development_2_2.R
import com.example.mobile_development_2_2.data.GoalDatabase

import com.example.mobile_development_2_2.gui.fragments.MapFragment
import com.example.mobile_development_2_2.gui.fragments.settings.SettingsFragment
import com.example.mobile_development_2_2.map.gps.GPSLocationProvider
import com.example.mobile_development_2_2.map.gps.GetLocationProvider
import com.example.mobile_development_2_2.ui.theme.MobileDevelopment2_2Theme
import com.example.mobile_development_2_2.ui.viewmodels.OSMViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import org.osmdroid.config.Configuration.*



class MainActivity : ComponentActivity() {
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 1
    lateinit var osmViewModel: OSMViewModel
    var map = MapFragment()
    private val TAG = "MainActivity"

    enum class Fragments(@StringRes val title: Int) {
        Map(title = R.string.mapScreen),
        History(title = R.string.historyScreen),
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))

        val db = GoalDatabase.getInstance(this.baseContext)


        setContent {

            val openDialog = remember {
                mutableStateOf(false)
            }

            MobileDevelopment2_2Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    MainScreen(openDialog, database = db)


                }
            }
        }
    }




    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        val permissionsToRequest = ArrayList<String>()
        var i = 0
        while (i < grantResults.size) {
            permissionsToRequest.add(permissions[i])
            i++
        }
        if (permissionsToRequest.size > 0) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                REQUEST_PERMISSIONS_REQUEST_CODE
            )
        }
    }


    fun CheckForDuplicateFragmentOnStack(navController: NavHostController) {
        if (navController.previousBackStackEntry!!.destination.displayName == navController.currentBackStackEntry!!.destination.displayName) {
            navController.popBackStack()
        }
    }

    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    fun MainScreen(
        openDialog: MutableState<Boolean>,
        navController: NavHostController = rememberNavController(),
        database: GoalDatabase
    ) {
        val premissions = rememberMultiplePermissionsState(
            listOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        )
        // Get current back stack entry
        val backStackEntry by navController.currentBackStackEntryAsState()

        val context = LocalContext.current
        val osmViewModel = remember {
            OSMViewModel(GetLocationProvider(GPSLocationProvider(context = context)), this)
        }
        this.osmViewModel = osmViewModel

        // Get the name of the current screen
        val currentScreen = Fragments.valueOf(
            backStackEntry?.destination?.route ?: Fragments.Map.name
        )

        Scaffold(
            topBar = {
                TopBar(
                    currentScreen = currentScreen,
                    canNavigateBack = navController.previousBackStackEntry != null,
                    navigateUp = { navController.navigateUp() },
                    onSettingsButtonClicked = { navController.navigate(Fragments.History.name) })
            },
            backgroundColor = MaterialTheme.colors.background,
            contentColor = MaterialTheme.colors.background
        ) { innerpadding ->
            NavHost(
                navController = navController,
                startDestination = Fragments.Map.name,
                modifier = Modifier
                    .padding(innerpadding)
                    .background(MaterialTheme.colors.background, RectangleShape)
            ) {
                composable(route = Fragments.Map.name) {
                    map.MapScreen(

                        viewModel = osmViewModel,
                        modifier = Modifier,
                        onPOIClicked = {
                            Log.d(TAG, "goal clicked")
                        },
                        database = database
                    )


                }
                composable(route = Fragments.History.name) {
                    SettingsFragment(
                        modifier = Modifier,
                        database = database
                    )
                }
            }
        }

    }

/*    @Preview(showBackground = true)
    @Composable
    fun MainScreenPreview() {
        MainScreen()
    }*/

    @Composable
    fun TopBar(
        currentScreen: MainActivity.Fragments,
        canNavigateBack: Boolean,
        navigateUp: () -> Unit,
        modifier: Modifier = Modifier,
        onSettingsButtonClicked: () -> Unit
    ) {
        TopAppBar(
            title = { Text(stringResource(currentScreen.title)) },
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        bottomEnd = 12.dp,
                        bottomStart = 12.dp
                    )
                )
                .background(
                    MaterialTheme.colors.background
                ),
            backgroundColor = MaterialTheme.colors.primary,
            contentColor = MaterialTheme.colors.onPrimary,
            actions = {
                if (currentScreen.name != Fragments.History.name) {
                    IconButton(onClick = { onSettingsButtonClicked() }) {
                        Icon(painterResource(id = R.drawable.ic_settings), contentDescription = "History")
                    }
                }

            },
            navigationIcon = {
                if (canNavigateBack) {
                    IconButton(onClick = navigateUp) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = ""
                        )
                    }
                }
            }
        )
    }
}
