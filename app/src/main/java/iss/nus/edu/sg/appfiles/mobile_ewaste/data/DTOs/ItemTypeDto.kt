package iss.nus.edu.sg.appfiles.mobile_ewaste.data.DTOs

import com.google.gson.annotations.SerializedName

data class ItemTypeDto(
    @SerializedName("itemTypeId") val itemTypeId: Int,
    @SerializedName("itemName") val itemName: String,
    @SerializedName("estimatedAvgWeight") val estimatedAvgWeight: Double
)