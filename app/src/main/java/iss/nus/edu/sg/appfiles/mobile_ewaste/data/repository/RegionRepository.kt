package iss.nus.edu.sg.appfiles.mobile_ewaste.data.repository

import iss.nus.edu.sg.appfiles.mobile_ewaste.network.RegionApi
import iss.nus.edu.sg.appfiles.mobile_ewaste.network.model.RegionDto

class RegionRepository(private val regionApi: RegionApi) {
    suspend fun getRegions(): List<RegionDto> = regionApi.getRegions()
}
