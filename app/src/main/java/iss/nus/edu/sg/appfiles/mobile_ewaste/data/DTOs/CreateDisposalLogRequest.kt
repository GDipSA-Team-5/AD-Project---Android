package iss.nus.edu.sg.appfiles.mobile_ewaste.data.DTOs

data class CreateDisposalLogRequest(
    val binId: Int?,
    val itemTypeId: Int,
    val serialNo: String,
    val estimatedWeightKg: Double,
    val feedback: String?,
    val userId: Int
)