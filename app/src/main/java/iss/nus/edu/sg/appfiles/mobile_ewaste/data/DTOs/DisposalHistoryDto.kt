package iss.nus.edu.sg.appfiles.mobile_ewaste.data.DTOs

data class DisposalHistoryDto (
    val logId: Int,
    val disposalTimeStamp: String,
    val estimatedTotalWeight: Double,
    val feedback: String?,

    val binId: Int?,
    val binLocationName: String?,

    val itemTypeId: Int,
    val itemTypeName: String?,
    val serialNo: String?,

    val categoryName: String?,
    val earnedPoints: Int,
    val locationName: String?
)