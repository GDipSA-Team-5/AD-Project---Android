package iss.nus.edu.sg.appfiles.mobile_ewaste.network.model

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val userId: Int?,
    val userName: String?,
    val token: String?
)
