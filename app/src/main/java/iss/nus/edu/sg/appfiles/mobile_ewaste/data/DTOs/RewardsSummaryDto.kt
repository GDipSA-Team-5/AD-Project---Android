package iss.nus.edu.sg.appfiles.mobile_ewaste.data.DTOs

data class RewardsSummaryDto (
    val totalPoints: Int,
    val expiringSoonPoints: Int,
    val nearestExpiryDate: String?,
    val totalDisposals: Int,
    val totalRedeemed: Int,
    val totalReferrals: Int
)
