package iss.nus.edu.sg.appfiles.mobile_ewaste.data.repository

import android.location.Location
import iss.nus.edu.sg.appfiles.mobile_ewaste.data.model.BinLocation
import iss.nus.edu.sg.appfiles.mobile_ewaste.data.model.Coordinate
import iss.nus.edu.sg.appfiles.mobile_ewaste.network.EwasteApi

class EwasteRepository(
    private val api: EwasteApi
) {
    suspend fun getNearbyBins(center: Coordinate, limit: Int = 3): List<BinLocation> {
        val bins = api.getBins()
            .mapNotNull { bin ->
                val latitude = bin.latitude
                val longitude = bin.longitude
                if (latitude == null || longitude == null) return@mapNotNull null

                val distance = distanceMeters(
                    center.latitude,
                    center.longitude,
                    latitude,
                    longitude
                )

                val description = buildDescription(
                    bin.estimatedFillLevel,
                    bin.riskLevel,
                )

                BinLocation(
                    binId = bin.binId,
                    name = bin.locationName?.takeIf { it.isNotBlank() } ?: "E-waste bin",
                    address = bin.locationAddress?.takeIf { it.isNotBlank() } ?: "Address unavailable",
                    description = "Predicted: ${bin.predictedStatus ?: "Unknown"}",
                    access = bin.binStatus?.takeIf { it.isNotBlank() } ?: "Active",
                    predictedStatus = bin.predictedStatus,
                    distanceMeters = distance,
                    estimatedFillLevel = bin.estimatedFillLevel,
                    riskLevel = bin.riskLevel,
                    daysToFull = bin.daysToFull,
                    coordinate = Coordinate(latitude = latitude, longitude = longitude)
                )
            }

        return bins.sortedBy { it.distanceMeters }.take(limit)
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

    private fun buildDescription(fillLevel: Double?, riskLevel: String?): String {
        return when {
            fillLevel != null -> "${fillLevel.toInt()}% full"
            else -> "Capacity unknown"
        }
    }

}
