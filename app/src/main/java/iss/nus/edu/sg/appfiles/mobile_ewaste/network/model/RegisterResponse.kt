package iss.nus.edu.sg.appfiles.mobile_ewaste.network.model

data class RegisterResponse(
    val success: Boolean,
    val message: String,
    val userId: Int?
)
