package iss.nus.edu.sg.appfiles.mobile_ewaste.data.repository

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import androidx.annotation.RequiresApi
import iss.nus.edu.sg.appfiles.mobile_ewaste.data.model.Coordinate
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.resume

class GeocodingRepository(
    private val context: Context,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO) {
    suspend fun geocodePostcode(postcode: String): Coordinate? {
        return withContext(ioDispatcher) {
            try {
                val geocoder = Geocoder(context, Locale.forLanguageTag("en-SG"))
                val address = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    geocodeAsync(geocoder, postcode)
                } else {
                    @Suppress("DEPRECATION")
                    geocoder.getFromLocationName(postcode, 1)?.firstOrNull()
                }
                address?.let { Coordinate(it.latitude, it.longitude) }
            } catch (_: Exception) {
                null
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private suspend fun geocodeAsync(geocoder: Geocoder, query: String): Address? {
        return suspendCancellableCoroutine { cont ->
            geocoder.getFromLocationName(query, 1, object : Geocoder.GeocodeListener {
                override fun onGeocode(addresses: MutableList<Address>) {
                    if (cont.isActive) cont.resume(addresses.firstOrNull())
                }

                override fun onError(errorMessage: String?) {
                    if (cont.isActive) cont.resume(null)
                }
            })
        }
    }
}
