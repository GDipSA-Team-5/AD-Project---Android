package iss.nus.edu.sg.appfiles.mobile_ewaste.network.model

data class GeoJsonResponse(
    val type: String?,
    val features: List<GeoJsonFeature>?
)

data class GeoJsonFeature(
    val type: String?,
    val geometry: GeoJsonGeometry?,
    val properties: GeoJsonProperties?
)

data class GeoJsonGeometry(
    val type: String?,
    val coordinates: List<Double>?
)

data class GeoJsonProperties(
    val NAME: String?,
    val ADDRESSBUILDINGNAME: String?,
    val ADDRESSSTREETNAME: String?,
    val ADDRESSPOSTALCODE: String?,
    val DESCRIPTION: String?,
    val ACCESSRESTRICTION: String?
)
