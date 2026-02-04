package iss.nus.edu.sg.appfiles.mobile_ewaste.data.DTOs

data class RewardRedemptionItemDto(
    val redemptionId: Int,
    val rewardId: Int,
    val rewardName: String,
    val imageUrl: String,
    val pointsUsed: Int,
    val redemptionStatus: String,
    val redemptionDateTime: String
)
