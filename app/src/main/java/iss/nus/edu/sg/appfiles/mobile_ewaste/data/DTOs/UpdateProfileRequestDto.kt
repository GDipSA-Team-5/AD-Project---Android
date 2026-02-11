package iss.nus.edu.sg.appfiles.mobile_ewaste.data.DTOs

import com.google.gson.annotations.SerializedName

data class UpdateProfileRequestDto(
    @SerializedName("phoneNumber") val phoneNumber: String?,
    @SerializedName("regionId") val regionId: Int?
)
