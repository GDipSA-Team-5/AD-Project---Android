package iss.nus.edu.sg.appfiles.mobile_ewaste.data.DTOs

data class RewardsHistoryDto (
    val transactionId: Int,
    val title: String,
    val categoryName:String,
    val points: Int,            // +50, -100 (if redeem)
    val createdAt: String,      // ISO string
)