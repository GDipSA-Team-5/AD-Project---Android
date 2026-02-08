package iss.nus.edu.sg.appfiles.mobile_ewaste.fragments

data class DisposeFormInput(
    val categoryPosition: Int,
    val itemTypePosition: Int,
    val serialNo: String,
    val weightText: String
)

object DisposeFormValidator {
    fun validate(input: DisposeFormInput): String? {
        if (input.categoryPosition == 0) return "Select category"
        if (input.itemTypePosition == 0) return "Select item type"
        if (input.serialNo.isBlank()) return "Serial number required"
        if (input.weightText.isBlank()) return "Estimated weight required"
        if (input.weightText.toDoubleOrNull() == null) return "Invalid weight"
        return null
    }
}
