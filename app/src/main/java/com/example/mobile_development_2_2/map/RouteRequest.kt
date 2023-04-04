package com.example.mobile_development_2_2.map


import android.util.Log
import com.google.gson.JsonArray
import kotlinx.coroutines.delay
import org.osmdroid.util.GeoPoint
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class RouteRequest {


    companion object {
        val LOG_TAG = "RouteRequest"
        var apikeys = arrayOf(  "5b3ce3597851110001cf6248b735223061bc413a910d3479e9b2ce91",
                                "5b3ce3597851110001cf62482fbd8d2e62ee41aab8811bbd5ae52f6a",
                                "5b3ce3597851110001cf62488c22c42514ed4d91876ac149c5e56b15",
                                "5b3ce3597851110001cf6248907d6528bd58464f813ae78086085f48",
                                "5b3ce3597851110001cf62489aa6cbff57bc434dab2b62f3bd5b7861"

        )

        suspend fun getRoute(origin: GeoPoint, destination: GeoPoint, number: Int?): String {
            var u_number = 0
            Log.d(LOG_TAG, "Getting route")

            if (number != null) {
                u_number = number
            }

            val url: URL =
                URL("https://api.openrouteservice.org/v2/directions/foot-walking/geojson")
            val conn: HttpURLConnection = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty(
                "Authorization",
                apikeys[u_number]
            )
            conn.setRequestProperty(
                "Accept",
                "application/json, application/geo+json, application/gpx+xml, img/png; charset=utf-8"
            )
            conn.setRequestProperty("Content-Type", "application/json; charset=utf-8")
            conn.doOutput = true
//
//
            val payload: String = """{"coordinates":[[${origin.longitude},${origin.latitude}],[${destination.longitude},${destination.latitude}]]}"""
            //Log.d(LOG_TAG, payload)
            val dataOutputStream = DataOutputStream(conn.outputStream)
            dataOutputStream.writeBytes(payload)
            dataOutputStream.flush()
            dataOutputStream.close()


            val responseCode: Int = conn.responseCode
            if(responseCode != 200){
                Log.e(LOG_TAG, "Error: $responseCode")
                 var apikey__ = "5b3ce3597851110001cf62482fbd8d2e62ee41aab8811bbd5ae52f6a"
                delay(1000)
                if(u_number == apikeys.size){
                    return("")
                }
                return getRoute(origin, destination,u_number+1)

            }
            Log.d(LOG_TAG,"Response Code: $responseCode")

            val inputStreamReader = InputStreamReader(conn.inputStream)
            val bufferedReader = BufferedReader(inputStreamReader)
            var inputLine: String?
            val response = StringBuilder()
            while (bufferedReader.readLine().also { inputLine = it } != null) {
                response.append(inputLine)
            }
            bufferedReader.close()
            //Log.d(LOG_TAG, ""+response)
            return (response.toString())
        }
    }
}