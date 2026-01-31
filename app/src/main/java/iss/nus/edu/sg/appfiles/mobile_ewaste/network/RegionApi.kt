package iss.nus.edu.sg.appfiles.mobile_ewaste.network

import iss.nus.edu.sg.appfiles.mobile_ewaste.network.model.RegionDto
import retrofit2.http.GET

interface RegionApi {
    @GET("api/auth/regions")
    suspend fun getRegions(): List<RegionDto>
}
