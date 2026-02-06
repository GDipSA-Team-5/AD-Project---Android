package iss.nus.edu.sg.appfiles.mobile_ewaste.data.DTOs

import com.google.gson.annotations.SerializedName

data class BinDto(
    @SerializedName("binId") val binId: Int,
    @SerializedName("regionId") val regionId: Int?,
    @SerializedName("locationName") val locationName: String?,
    @SerializedName("locationAddress") val locationAddress: String?,
    @SerializedName("binStatus") val binStatus: String?,
    @SerializedName("latitude") val latitude: Double?,
    @SerializedName("longitude") val longitude: Double?,
    @SerializedName("estimatedFillLevel") val estimatedFillLevel: Double?,
    @SerializedName("riskLevel") val riskLevel: String?,
    @SerializedName("daysToFull") val daysToFull: Int?,
    @SerializedName("predictedStatus") val predictedStatus: String?
)
