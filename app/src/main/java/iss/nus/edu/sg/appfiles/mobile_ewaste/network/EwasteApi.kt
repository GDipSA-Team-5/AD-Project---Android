package iss.nus.edu.sg.appfiles.mobile_ewaste.network

import iss.nus.edu.sg.appfiles.mobile_ewaste.data.DTOs.BinDto
import iss.nus.edu.sg.appfiles.mobile_ewaste.data.DTOs.CategoryDto
import iss.nus.edu.sg.appfiles.mobile_ewaste.data.DTOs.CreateDisposalLogRequest
import iss.nus.edu.sg.appfiles.mobile_ewaste.data.DTOs.DisposalHistoryDto
import iss.nus.edu.sg.appfiles.mobile_ewaste.data.DTOs.ItemTypeDto
import iss.nus.edu.sg.appfiles.mobile_ewaste.data.DTOs.RewardsHistoryDto
import iss.nus.edu.sg.appfiles.mobile_ewaste.data.DTOs.RewardsSummaryDto
import iss.nus.edu.sg.appfiles.mobile_ewaste.data.DTOs.RewardWalletDto
import iss.nus.edu.sg.appfiles.mobile_ewaste.data.DTOs.RewardCatalogueDto
import iss.nus.edu.sg.appfiles.mobile_ewaste.data.DTOs.RedeemRequestDto
import iss.nus.edu.sg.appfiles.mobile_ewaste.data.DTOs.RedeemResponseDto
import iss.nus.edu.sg.appfiles.mobile_ewaste.data.DTOs.RewardRedemptionItemDto
import iss.nus.edu.sg.appfiles.mobile_ewaste.data.DTOs.UserProfileDto
import iss.nus.edu.sg.appfiles.mobile_ewaste.network.model.LoginRequest
import iss.nus.edu.sg.appfiles.mobile_ewaste.network.model.LoginResponse
import iss.nus.edu.sg.appfiles.mobile_ewaste.network.model.RegisterRequest
import iss.nus.edu.sg.appfiles.mobile_ewaste.network.model.RegisterResponse
import iss.nus.edu.sg.appfiles.mobile_ewaste.network.model.RegionDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface EwasteApi {
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @GET("api/auth/regions")
    suspend fun getRegions(): List<RegionDto>

    @GET("api/auth/profile")
    suspend fun  getUser(@Query("userId")userId: Int): UserProfileDto

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

    @GET("api/rewards/wallet")
    suspend fun getRewardWallet(
        @Query("userId")userId: Int): RewardWalletDto

    @GET("api/rewards/catalogue")
    suspend fun getRewardCatalogue(): List<RewardCatalogueDto>

    @POST("api/rewards/redeem")
    suspend fun redeemReward(@Body request: RedeemRequestDto): RedeemResponseDto

    @GET("api/rewards/redemptions")
    suspend fun getRewardRedemptions(
        @Query("userId")userId: Int): List<RewardRedemptionItemDto>
}
