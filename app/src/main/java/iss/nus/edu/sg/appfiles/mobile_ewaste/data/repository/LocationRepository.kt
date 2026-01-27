package iss.nus.edu.sg.appfiles.mobile_ewaste.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import iss.nus.edu.sg.appfiles.mobile_ewaste.data.model.Coordinate
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class LocationRepository(context: Context) {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    suspend fun getBestLocation(): Coordinate? {
        return try {
            val current = getCurrentLocation()
            if (current != null) {
                Coordinate(current.latitude, current.longitude)
            } else {
                val last = getLastLocation()
                if (last != null) Coordinate(last.latitude, last.longitude) else null
            }
        } catch (ex: SecurityException) {
            null
        }
    }

    @SuppressLint("MissingPermission")
    private suspend fun getCurrentLocation(): Location? {
        val tokenSource = CancellationTokenSource()
        return suspendCancellableCoroutine { continuation ->
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, tokenSource.token)
                .addOnSuccessListener { continuation.resume(it) }
                .addOnFailureListener { continuation.resume(null) }
        }
    }

    @SuppressLint("MissingPermission")
    private suspend fun getLastLocation(): Location? {
        return suspendCancellableCoroutine { continuation ->
            fusedLocationClient.lastLocation
                .addOnSuccessListener { continuation.resume(it) }
                .addOnFailureListener { continuation.resume(null) }
        }
    }
}
