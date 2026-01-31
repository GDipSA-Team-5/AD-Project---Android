package iss.nus.edu.sg.appfiles.mobile_ewaste.data.DTOs

import com.google.gson.annotations.SerializedName

data class BinDto(
    @SerializedName("binId") val binId: Int,
    @SerializedName("locationName") val locationName: String?
)