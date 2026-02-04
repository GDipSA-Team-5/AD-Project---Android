package iss.nus.edu.sg.appfiles.mobile_ewaste.data.DTOs

data class RedeemResponseDto(
    val success: Boolean,
    val message: String,
    val remainingPoints: Int,
    val redemptionId: Int
)
