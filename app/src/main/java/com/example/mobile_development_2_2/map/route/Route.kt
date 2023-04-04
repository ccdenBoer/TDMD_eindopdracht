package com.example.mobile_development_2_2.map.route

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.mobile_development_2_2.R

import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Overlay

data class Route(
    val name : String,
    val cityName: String,
    val length: Double,
    val img: String = "ic_logo.png",
    val shortDescription : String,
    val POIs: List<POI>,
    var finished: MutableState<Boolean> = mutableStateOf(false),
    var started: MutableState<Boolean> = mutableStateOf(false),
    var currentLength: MutableState<Double> = mutableStateOf(0.0),
    var totalPoisVisited: MutableState<Int> = mutableStateOf(0)


    ) {

    fun addPOI(poi: POI){
        POIs.plus(poi)
    }

    fun hasProgress(): Boolean{
        for(poi in POIs ){
            if(poi.visited)
                return true
        }
        return false
    }

    fun updateLength(){
        currentLength.value = 0.0

        for(poi in POIs){
            if(poi.visited){
                //Log.d("Route", "${currentLength.value} en ${poi.length} en ${poi.name}")
                currentLength.value += poi.length
            }
        }
    }

    fun resetProgress(){
        for(poi in POIs ){
            poi.visited = false
        }
        currentLength.value = 0.0
        finished.value = false
        totalPoisVisited.value = 0

    }

    fun totalPoisVisited(): Int{
        var i = 0
        POIs.forEach {
            if (it.visited) {
                i++
            }
        }

        return i
    }

    fun getTotalLength(): Double{
        var length = 0.0

        for(poi in POIs){
            length += poi.length

        }

        return length
    }


    companion object{
        fun TestRoute(): Route{
            return TestRoute("testRoute")
        }


        fun TestRoute(name: String): Route {
            val avans = POI(
                name = "Avans",
                visited = false,
                location = GeoPoint(51.5856, 4.7925),
                img = "img_poi1.jpg",
                streetName = "street1",
                shortDescription = "description of Avans",
                longDescription = "safasda",
                imgMap = "ic_map.png",
                length = 1.0
            )

            // TODO: Move to POI repository
            val breda = POI(
                name = "Breda",
                visited = false,
                location = GeoPoint(51.5719, 4.7683),
                img = "img_poi3.jpg",
                streetName = "street2",
                shortDescription = "description of Breda",
                longDescription = "safasda",
                imgMap = "ic_map.png",
                length = 1.0

            )

            // TODO: Move to POI repository
            val amsterdam = POI(
                name = "Amsterdam",
                visited = false,
                location = GeoPoint(52.3676, 4.9041),
                img = "img_poi3.jpg",
                streetName = "street3",
                shortDescription = "description of Amsterdam 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15",
                longDescription = "safasda",
                imgMap = "ic_map.png",
                length = 1.0

            )

            // TODO: Move to POI repository
            val cities = listOf(
                avans,
                breda,
                amsterdam,
            )


            return Route(
                name = name,
                cityName = "Breda",
                length = 6.234,
                img = "img_poi3.jpg",
                shortDescription = "A route for testing",
                POIs = cities,

            )

        }
        fun TestRoute2(name: String): Route {
            val avans = POI(
                name = "Avans2",
                visited = false,
                location = GeoPoint(51.5856, 4.7925),
                img = "img_poi3.jpg",
                streetName = "street2.1",
                shortDescription = "description of Avans2",
                longDescription = "safasda2",
                imgMap = "ic_map.png",
                length = 1.0


            )

            // TODO: Move to POI repository
            val breda = POI(
                name = "Breda2",
                visited = false,
                location = GeoPoint(51.5719, 4.7683),
                img = "img_poi3.jpg",
                streetName = "street2.2",
                shortDescription = "description of Breda2",
                longDescription = "safasda2",
                imgMap = "ic_map.png",
                length = 1.0
            )

            // TODO: Move to POI repository
            val amsterdam = POI(
                name = "Amsterdam2",
                visited = false,
                location = GeoPoint(52.3676, 4.9041),
                img = "img_poi3.jpg",
                streetName = "street2.3",
                shortDescription = "description of Amsterdam 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15",
                longDescription = "safasda2",
                imgMap = "ic_map.png",
                length = 1.0
            )

            // TODO: Move to POI repository
            val cities = listOf(
                avans,
                breda,
                amsterdam,
            )


            return Route(
                name = name,
                cityName = "Breda2",
                length = 6.234,
                img = "img_poi3.jpg",
                shortDescription = "A route for testing2",
                POIs = cities,

            )

        }

        var selectedItem = TestRoute().POIs[0]

        fun selectItem(poi: POI){
            Log.d("a", "Item selected")
            selectedItem = poi
        }

        @JvmName("getSelectedItem1")
        fun getSelectedPOI(): POI {
            return selectedItem
        }
    }

}
