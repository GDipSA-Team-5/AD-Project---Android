package iss.nus.edu.sg.appfiles.mobile_ewaste.data.DTOs

data class ItemTypeDto(
        val itemTypeId: Int,
        val itemName: String,
    val estimatedAvgWeight: Double
){
    override fun toString(): String = itemName
    }