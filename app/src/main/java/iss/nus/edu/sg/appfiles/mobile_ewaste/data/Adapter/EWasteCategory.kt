package iss.nus.edu.sg.appfiles.mobile_ewaste.data.Adapter

import iss.nus.edu.sg.appfiles.mobile_ewaste.R

enum class EWasteCategory(val displayNames: List<String>, val iconRes: Int) {
    SMALL_CONSUMER_ELECTRONICS(
        listOf("Small Consumer Electronics"),
        R.drawable.ic_phone
    ),

    SMALL_HOUSEHOLD_APPLIANCES(
        listOf("Small Household Appliances"),
        R.drawable.ic_appliance
    ),

    SMALL_IT_COMMUNICATION(
        listOf("Small IT & Communication Devices"),
        R.drawable.ic_laptop
    ),

    CONSUMER_ELECTRONICS(
        listOf("Consumer Electronics"),
        R.drawable.ic_tv
    ),

    LIGHTING_EQUIPMENT(
        listOf("Lighting Equipment"),
        R.drawable.ic_bulb
    ),

    ELECTRICAL_TOOLS(
        listOf("Electrical & Electronic Tools"),
        R.drawable.ic_tools
    ),

    OTHER(
        emptyList(),
        R.drawable.ic_medal
    );

    companion object {
        fun from(categoryName: String?): EWasteCategory {
            if (categoryName == null) return OTHER

            return entries.firstOrNull { category ->
                category.displayNames.any {
                    it.equals(categoryName, ignoreCase = true)
                }
            } ?: OTHER
        }
    }
}