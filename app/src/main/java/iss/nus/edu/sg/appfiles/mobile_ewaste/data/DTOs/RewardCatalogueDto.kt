package iss.nus.edu.sg.appfiles.mobile_ewaste.data.DTOs

data class RewardCatalogueDto(
    val rewardId: Int,
    val rewardName: String,
    val description: String,
    val points: Int,
    val rewardCategory: String,
    val stockQuantity: Int,
    val imageUrl: String,
    val availability: Boolean
)
