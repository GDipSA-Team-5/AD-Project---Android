package iss.nus.edu.sg.appfiles.mobile_ewaste.data.model

data class BinLocation(
    val binId: Int,
    val name: String,
    val address: String,
    val description: String,
    val access: String,
    val predictedStatus: String?,
    val distanceMeters: Float,
    val coordinate: Coordinate
)
