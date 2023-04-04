package com.example.mobile_development_2_2.data

import org.osmdroid.util.GeoPoint
import java.io.IOException
import java.io.ObjectOutputStream
import java.net.Socket

class Client(private val serverAddress: String, private val serverPort: Int) {
    fun sendGeoLocation(geopoint: GeoPoint) {
        try {
            // Create a socket connection to the server
            val socket = Socket(serverAddress, serverPort)
            val location = "${geopoint.latitude},${geopoint.longitude}"
            // Send the GeoLocation to the server
            val outputStream = socket.getOutputStream()
            val objectOutputStream = ObjectOutputStream(outputStream)
            objectOutputStream.writeObject(location)
            objectOutputStream.flush()

            // Close the socket connection
            socket.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}