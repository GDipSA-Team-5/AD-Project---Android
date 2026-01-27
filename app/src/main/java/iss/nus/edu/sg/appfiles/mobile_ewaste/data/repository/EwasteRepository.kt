package iss.nus.edu.sg.appfiles.mobile_ewaste.data.repository

import android.content.Context
import android.location.Location
import com.google.gson.Gson
import iss.nus.edu.sg.appfiles.mobile_ewaste.data.model.BinLocation
import iss.nus.edu.sg.appfiles.mobile_ewaste.data.model.Coordinate
import iss.nus.edu.sg.appfiles.mobile_ewaste.network.DataGovApi
import iss.nus.edu.sg.appfiles.mobile_ewaste.network.await
import iss.nus.edu.sg.appfiles.mobile_ewaste.network.model.GeoJsonFeature
import iss.nus.edu.sg.appfiles.mobile_ewaste.network.model.GeoJsonProperties
import iss.nus.edu.sg.appfiles.mobile_ewaste.network.model.GeoJsonResponse
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.util.Locale

class EwasteRepository(
    private val context: Context,
    private val api: DataGovApi
) {
    private val gson = Gson()

    suspend fun getNearbyBins(center: Coordinate, limit: Int = 3): List<BinLocation> {
        val geoJson = getGeoJson() ?: return emptyList()
        val bins = buildBins(geoJson.features.orEmpty(), center)
        return bins.sortedBy { it.distanceMeters }.take(limit)
    }

    private suspend fun getGeoJson(): GeoJsonResponse? {
        val cached = readCachedGeoJson()
        if (isCacheValid() && cached != null) {
            return cached
        }

        val remote = fetchRemoteGeoJson()
        if (remote != null) {
            writeCachedGeoJson(remote)
            return remote
        }

        return cached
    }

    private suspend fun fetchRemoteGeoJson(): GeoJsonResponse? {
        return try {
            val poll = api.pollDownload(DATASET_ID).await()
            val url = poll.data?.url
            if (poll.code != 0 || url.isNullOrBlank()) {
                null
            } else {
                api.downloadGeoJson(url).await()
            }
        } catch (ex: Exception) {
            null
        }
    }

    private fun buildBins(
        features: List<GeoJsonFeature>,
        center: Coordinate
    ): List<BinLocation> {
        return features.mapNotNull { feature ->
            val coords = feature.geometry?.coordinates
            if (coords == null || coords.size < 2) return@mapNotNull null
            val longitude = coords[0]
            val latitude = coords[1]
            val props = feature.properties

            val distance = distanceMeters(
                center.latitude,
                center.longitude,
                latitude,
                longitude
            )

            BinLocation(
                name = props?.NAME?.takeIf { it.isNotBlank() } ?: "E-waste bin",
                address = formatAddress(props),
                description = props?.DESCRIPTION?.takeIf { it.isNotBlank() } ?: "Collection bin",
                access = props?.ACCESSRESTRICTION?.takeIf { it.isNotBlank() } ?: "Public",
                distanceMeters = distance,
                coordinate = Coordinate(latitude = latitude, longitude = longitude)
            )
        }
    }

    private fun distanceMeters(
        startLat: Double,
        startLng: Double,
        endLat: Double,
        endLng: Double
    ): Float {
        val results = FloatArray(1)
        Location.distanceBetween(startLat, startLng, endLat, endLng, results)
        return results[0]
    }

    private fun formatAddress(props: GeoJsonProperties?): String {
        val building = props?.ADDRESSBUILDINGNAME?.trim().orEmpty()
        val street = props?.ADDRESSSTREETNAME?.trim().orEmpty()
        val postal = props?.ADDRESSPOSTALCODE?.trim().orEmpty()

        val parts = listOf(building, street).filter { it.isNotBlank() }
        val base = if (parts.isEmpty()) "Address unavailable" else parts.joinToString(", ")
        return if (postal.isNotBlank()) "$base (S$postal)" else base
    }

    private fun readCachedGeoJson(): GeoJsonResponse? {
        val file = getCacheFile()
        if (!file.exists()) return null
        return try {
            FileReader(file).use { reader ->
                gson.fromJson(reader, GeoJsonResponse::class.java)
            }
        } catch (ex: Exception) {
            null
        }
    }

    private fun writeCachedGeoJson(geoJson: GeoJsonResponse) {
        val file = getCacheFile()
        try {
            FileWriter(file).use { writer ->
                gson.toJson(geoJson, writer)
            }
            val prefs = context.getSharedPreferences(CACHE_PREFS, Context.MODE_PRIVATE)
            prefs.edit().putLong(CACHE_TIME_KEY, System.currentTimeMillis()).apply()
        } catch (ex: Exception) {
            // Ignore cache write failures
        }
    }

    private fun isCacheValid(): Boolean {
        val prefs = context.getSharedPreferences(CACHE_PREFS, Context.MODE_PRIVATE)
        val cachedAt = prefs.getLong(CACHE_TIME_KEY, 0L)
        return cachedAt > 0 && System.currentTimeMillis() - cachedAt < CACHE_TTL_MS
    }

    private fun getCacheFile(): File {
        return File(context.filesDir, CACHE_FILE_NAME)
    }

    companion object {
        private const val DATASET_ID = "d_db40d004afeb5a7f0f555fdcc34934cc"
        private const val CACHE_FILE_NAME = "ewaste_geojson_cache.json"
        private const val CACHE_PREFS = "ewaste_cache"
        private const val CACHE_TIME_KEY = "geojson_cached_at"
        private const val CACHE_TTL_MS = 7L * 24 * 60 * 60 * 1000
    }
}
