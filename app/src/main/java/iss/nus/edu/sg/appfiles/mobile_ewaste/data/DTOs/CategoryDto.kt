package iss.nus.edu.sg.appfiles.mobile_ewaste.data.DTOs

data class CategoryDto(val categoryId: Int, val categoryName:String) {
    override fun toString(): String = categoryName
}