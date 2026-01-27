package iss.nus.edu.sg.appfiles.mobile_ewaste.data.repository

import android.content.Context
import android.location.Geocoder
import iss.nus.edu.sg.appfiles.mobile_ewaste.data.model.Coordinate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

class GeocodingRepository(private val context: Context) {
    suspend fun geocodePostcode(postcode: String): Coordinate? {
        return withContext(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context, Locale("en", "SG"))
                val results = geocoder.getFromLocationName(postcode, 1)
                val address = results?.firstOrNull()
                if (address != null) {
                    Coordinate(address.latitude, address.longitude)
                } else {
                    null
                }
            } catch (ex: Exception) {
                null
            }
        }
    }
}
