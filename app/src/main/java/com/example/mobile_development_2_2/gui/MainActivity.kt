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
import com.example.mobile_development_2_2.R

import com.example.mobile_development_2_2.data.Lang
import com.example.mobile_development_2_2.data.PopupHelper
import com.example.mobile_development_2_2.gui.fragments.home.HelpItem
import com.example.mobile_development_2_2.gui.fragments.home.HomeScreen
import com.example.mobile_development_2_2.gui.fragments.home.InfoScreen
import com.example.mobile_development_2_2.gui.fragments.poi.POIDetailScreen
import com.example.mobile_development_2_2.gui.fragments.poi.POIListScreen
import com.example.mobile_development_2_2.gui.fragments.route.RouteListScreen
import com.example.mobile_development_2_2.map.route.RouteManager
import com.example.mobile_development_2_2.gui.fragments.MapFragment
import com.example.mobile_development_2_2.gui.fragments.settings.SettingsFragment
import com.example.mobile_development_2_2.map.gps.GPSLocationProvider
import com.example.mobile_development_2_2.map.gps.GetLocationProvider
import com.example.mobile_development_2_2.map.route.Route
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

    private val isPipSupported by lazy {
        packageManager.hasSystemFeature(
            PackageManager.FEATURE_PICTURE_IN_PICTURE
        )
    }

    enum class Fragments(@StringRes val title: Int) {
        Home(title = R.string.homeScreen),
        Info(title = R.string.infoScreen),
        POIList(title = R.string.poiListScreen),
        POI(title = R.string.POIScreen),
        Route(title = R.string.routeScreen),
        Map(title = R.string.mapScreen),
        Settings(title = R.string.settingsScreen),
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))
        Lang.setContext(this)
        Lang.onLanguageChanged { recreate() }
        Lang.onColorblindChange {  }
        Lang.loadSettings()

        RouteManager.getRouteManager(this)


        setContent {

            //Log.d("Mainactivity", RouteManager.getrouteManager(this).getStringById("HelpItem1"))

            val openDialog = remember {
                mutableStateOf(false)
            }
            val openMapDialog = remember {
                mutableStateOf(false)
            }

            PopupHelper.SetState(openDialog)

            MobileDevelopment2_2Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    MainScreen(openDialog, openMapDialog)


                }
            }
        }
    }
    override fun onUserLeaveHint() {
        if (!isPipSupported)
            return
        super.onUserLeaveHint()
        enterPictureInPictureMode()

    }
    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration
    ) {
        if(isInPictureInPictureMode) {
            
        } else {

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
        openMapDialog: MutableState<Boolean>,
        navController: NavHostController = rememberNavController()
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
            backStackEntry?.destination?.route ?: Fragments.Home.name
        )

        Scaffold(
            topBar = {
                TopBar(
                    currentScreen = currentScreen,
                    canNavigateBack = navController.previousBackStackEntry != null,
                    navigateUp = { navController.navigateUp() },
                    onSettingsButtonClicked = { navController.navigate(Fragments.Settings.name) })
            },
            bottomBar = {
                BottomNavigationBar(
                    onHomeButtonClicked = {
                        navController.backQueue.clear()
                        navController.navigate(Fragments.Home.name)
                    },
                    onHomePOIClicked = {
                        navController.backQueue.clear()
                        navController.navigate(Fragments.POIList.name)
                    },
                    onMapButtonClicked = {
                        navController.backQueue.clear()
                        navController.navigate(Fragments.Route.name)
                        premissions.launchMultiplePermissionRequest()
                        Log.d("123", "map")
                    }
                )
            },
            backgroundColor = MaterialTheme.colors.background,
            contentColor = MaterialTheme.colors.background
        ) { innerpadding ->
            NavHost(
                navController = navController,
                startDestination = Fragments.Home.name,
                modifier = Modifier
                    .padding(innerpadding)
                    .background(MaterialTheme.colors.background, RectangleShape)
            ) {
                composable(route = Fragments.Home.name) {
                    HomeScreen(
                        modifier = Modifier,
                        helpItems = HelpItem.getItems(),
                        onPOIButtonClicked = {
                            navController.navigate(Fragments.Info.name)

                        })

                }
                composable(route = Fragments.Route.name) {
                    RouteListScreen(
                        modifier = Modifier,
                        routes = RouteManager.getRouteManager(baseContext).GetRoutes(),
                        onRouteClicked = {
                            Log.d("route", RouteManager.getRouteManager(baseContext).getSelectedRoute().name)
                            if(RouteManager.getRouteManager(null).getSelectedRoute().hasProgress()){
                                openMapDialog.value = true
                            } else{
                                navController.navigate(Fragments.Map.name)
                            }



                        },
                        onPOIClicked = {
                            navController.navigate(Fragments.POIList.name)
                        }
                    )
                }
                composable(route = Fragments.POIList.name) {
                    POIListScreen(
                        modifier = Modifier,
                        route = RouteManager.getRouteManager(baseContext).getSelectedRoute(),
                        onPOIClicked = {
                            navController.navigate(Fragments.POI.name)
                        }
                    )
                }
                composable(route = Fragments.Info.name) {
                    InfoScreen(
                        modifier = Modifier,
                        helpItem = HelpItem.getSelectedItem()
                    )
                }
                composable(route = Fragments.POI.name) {
                    POIDetailScreen(
                        modifier = Modifier,
                        poi = RouteManager.getRouteManager(baseContext).getSelectedPOI()
                    )
                }
                composable(route = Fragments.Map.name) {
                    map.MapScreen(

                        viewModel = osmViewModel,
                        modifier = Modifier,
                        onPOIClicked = {
                            navController.navigate(Fragments.POI.name)
                        }
                    )
//                   map.setRoute(RouteManager.getSelectedRoute().name)


                }
                composable(route = Fragments.Settings.name) {
                    SettingsFragment(
//                        viewModel = osmViewModel,
                        modifier = Modifier
                    )
                }
            }

            if (openDialog.value) {
                popUp("e", "e", openDialog) { navController.navigate(Fragments.POI.name) }
            }

            if (openMapDialog.value) {
                mapPopUp(
                    openMapDialog ,
                {
                    navController.navigate(Fragments.Map.name)
                },
                {
                    RouteManager.getRouteManager(null).getSelectedRoute().resetProgress()
                    navController.navigate(Fragments.Map.name)
                })
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
        val item = NavigationItem.Settings
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
                if (currentScreen.name != Fragments.Settings.name) {
                    IconButton(onClick = { onSettingsButtonClicked() }) {
                        Icon(painterResource(id = item.icon), contentDescription = item.title)
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

/*    @Preview(showBackground = true)
    @Composable
    fun TopBarPreview() {
        TopBar(true, {})
    }*/


    @Composable
    fun BottomNavigationBar(
        onHomeButtonClicked: () -> Unit,
        onMapButtonClicked: () -> Unit,
        onHomePOIClicked: () -> Unit
    ) {
        val items = listOf(
            NavigationItem.Home,
            NavigationItem.Map,
            NavigationItem.POIs,
        )

        BottomNavigation(
            backgroundColor = MaterialTheme.colors.primary,
            contentColor = MaterialTheme.colors.onPrimary,
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 12.dp,
                        topEnd = 12.dp
                    )
                )
                .background(
                    MaterialTheme.colors.background
                )
                .height(70.dp),
        ) {
            items.forEach { item ->
                var onClick = onHomeButtonClicked

                if (item.route.equals("map")) {
                    onClick = onMapButtonClicked

                } else if (item.route.equals("home")) {
                    onClick = onHomeButtonClicked

                } else if (item.route.equals("POIs")) {
                    onClick = onHomePOIClicked

                }

                BottomNavigationItem(
                    icon = {
                        Icon(
                            painterResource(id = item.icon),
                            contentDescription = item.title,
                            modifier = Modifier.size(40.dp),
                            tint = MaterialTheme.colors.onPrimary
                        )
                    },
                    label = { Text(
                        text = item.title,
                        color = MaterialTheme.colors.onPrimary
                    ) },
                    selectedContentColor = MaterialTheme.colors.primary,
                    unselectedContentColor = MaterialTheme.colors.surface,
                    alwaysShowLabel = true,
                    selected = false,
                    onClick = onClick,
                    modifier = Modifier

                )
            }
        }
    }

    @Composable
    fun popUp(
        title: String,
        text: String,
        openDialog: MutableState<Boolean>,
        onYesButtonClicked: () -> Unit
    ) {

        Log.d("main", "opening popup")

        AlertDialog(
            onDismissRequest = { !openDialog.value },
            title = { Text(text = RouteManager.getRouteManager(baseContext).previousTargetPOI.name, color = Color.Black) },
            text = {
                Text(
                    text = RouteManager.getRouteManager(null).getStringByName(RouteManager.getRouteManager(baseContext).previousTargetPOI.shortDescription),
                    color = Color.Black
                )
            },

            confirmButton = {
                TextButton(
                    onClick = {
                        openDialog.value = false
                        RouteManager.getRouteManager(null).selectPOI(RouteManager.getRouteManager(null).previousTargetPOI)
                        onYesButtonClicked()
                    }
                ) {
                    Text(text = Lang.get(R.string.confirm), color = Color.Blue)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        openDialog.value = false
                    }
                ) {
                    Text(text = Lang.get(R.string.cancel), color = Color.Blue)
                }
            },
            backgroundColor = Color.White,
            contentColor = Color.Black
        )

    }

    @Composable
    fun mapPopUp(
        openMapDialog: MutableState<Boolean>,
        onContinueButtonClicked: () -> Unit,
        onResetButtonClicked: () -> Unit
    ) {

        Log.d("main", "opening map popup")

        AlertDialog(
            onDismissRequest = { !openMapDialog.value },
            title = { Text(text = RouteManager.getRouteManager(baseContext).previousTargetPOI.name, color = Color.Black) },
            text = {
                Text(
                    text = Lang.get(R.string.mapPopup),
                    color = Color.Black
                )
            },

            confirmButton = {
                TextButton(
                    onClick = {
                        openMapDialog.value = false
                        onContinueButtonClicked()
                    }
                ) {
                    Text(text = Lang.get(R.string.popupcontinue), color = Color.Blue)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        openMapDialog.value = false
                        onResetButtonClicked()
                    }
                ) {
                    Text(text = Lang.get(R.string.popupreset), color = Color.Blue)
                }
            },
            backgroundColor = Color.White,
            contentColor = Color.Black
        )

    }

    override fun onStop() {
        RouteManager.getRouteManager(null).saveRoutesProgress()
        Lang.saveSettings()

        super.onStop()
    }
}


/*    @Preview(showBackground = true)
    @Composable
    fun BottomNavigationBarPreview() {
        BottomNavigationBar()
    }*/
