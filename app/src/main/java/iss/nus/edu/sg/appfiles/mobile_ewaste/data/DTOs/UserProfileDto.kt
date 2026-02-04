package iss.nus.edu.sg.appfiles.mobile_ewaste.data.DTOs

import com.google.gson.annotations.SerializedName

data class UserProfileDto (
    @SerializedName("userId") val userId: Int,
    @SerializedName("userName") val userName: String?,
    @SerializedName("email") val userEmail:String?,
    @SerializedName("phoneNumber") val userPhoneNumber: String?
)