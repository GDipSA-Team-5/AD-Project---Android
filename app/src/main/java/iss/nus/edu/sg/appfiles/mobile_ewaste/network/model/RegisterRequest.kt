package iss.nus.edu.sg.appfiles.mobile_ewaste.network.model

data class RegisterRequest(
    val fullName: String,
    val email: String,
    val phone: String,
    val address: String,
    val password: String,
    val referralCode: String?
)
