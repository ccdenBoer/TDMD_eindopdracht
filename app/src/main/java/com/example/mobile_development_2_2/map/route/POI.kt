package com.example.mobile_development_2_2.map.route

import org.osmdroid.util.GeoPoint

data class POI (
    val name: String,
    val location: GeoPoint,
    var img : String,
    val streetName: String,
    var shortDescription: String,
    var longDescription: String,
    var imgMap : String = "ic_map.png",
    var visited : Boolean =false,
    var length: Double
){
    fun load(line: String){
        visited = line == "true"
    }

}

