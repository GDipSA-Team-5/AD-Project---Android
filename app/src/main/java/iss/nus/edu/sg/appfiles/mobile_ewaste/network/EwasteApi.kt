package iss.nus.edu.sg.appfiles.mobile_ewaste.network

import iss.nus.edu.sg.appfiles.mobile_ewaste.data.DTOs.BinDto
import iss.nus.edu.sg.appfiles.mobile_ewaste.data.DTOs.CategoryDto
import iss.nus.edu.sg.appfiles.mobile_ewaste.data.DTOs.CreateDisposalLogRequest
import iss.nus.edu.sg.appfiles.mobile_ewaste.data.DTOs.DisposalHistoryDto
import iss.nus.edu.sg.appfiles.mobile_ewaste.data.DTOs.ItemTypeDto
import iss.nus.edu.sg.appfiles.mobile_ewaste.data.DTOs.RewardsHistoryDto
import iss.nus.edu.sg.appfiles.mobile_ewaste.data.DTOs.RewardsSummaryDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface EwasteApi {

    @GET("api/lookup/bins")
    suspend fun getBins(): List<BinDto>

    @GET("api/lookup/categories")
    suspend fun getCategories(): List<CategoryDto>

    @GET("api/lookup/itemtypes")
    suspend fun getItemTypes(
        @Query("categoryId") categoryId: Int
    ): List<ItemTypeDto>

    @GET("api/disposallogs/history")
    suspend fun getDisposalHistory(
        @Query("userID") userId:Int,
        @Query("range")range:String = "all"): List<DisposalHistoryDto>
    @POST("api/disposallogs")
    suspend fun createDisposalLog(
        @Body request: CreateDisposalLogRequest
    )

    @GET("api/rewards/summary")
    suspend fun getRewardsSummary(
        @Query("userId")userId: Int): RewardsSummaryDto

    @GET("api/rewards/history")
    suspend fun getRewardsHistory(
        @Query("userId")userId:Int): List<RewardsHistoryDto>
}