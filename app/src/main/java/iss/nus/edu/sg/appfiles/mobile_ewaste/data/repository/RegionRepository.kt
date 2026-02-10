package iss.nus.edu.sg.appfiles.mobile_ewaste.data.repository

import iss.nus.edu.sg.appfiles.mobile_ewaste.network.EwasteApi
import iss.nus.edu.sg.appfiles.mobile_ewaste.network.model.RegionDto

class RegionRepository(private val ewasteApi: EwasteApi) {
    suspend fun getRegions(): List<RegionDto> = ewasteApi.getRegions()
}
