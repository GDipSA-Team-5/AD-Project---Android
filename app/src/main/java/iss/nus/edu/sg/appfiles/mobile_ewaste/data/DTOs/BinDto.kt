package iss.nus.edu.sg.appfiles.mobile_ewaste.data.DTOs

data class BinDto (
    val binId: Int,
    val binCode: String
){
    override fun toString(): String = binCode
}