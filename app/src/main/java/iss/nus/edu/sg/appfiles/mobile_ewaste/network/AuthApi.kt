package iss.nus.edu.sg.appfiles.mobile_ewaste.network

import iss.nus.edu.sg.appfiles.mobile_ewaste.network.model.RegisterRequest
import iss.nus.edu.sg.appfiles.mobile_ewaste.network.model.RegisterResponse
import iss.nus.edu.sg.appfiles.mobile_ewaste.network.model.LoginRequest
import iss.nus.edu.sg.appfiles.mobile_ewaste.network.model.LoginResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse
}
