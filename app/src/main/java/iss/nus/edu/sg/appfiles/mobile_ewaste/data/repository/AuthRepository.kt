package iss.nus.edu.sg.appfiles.mobile_ewaste.data.repository

import iss.nus.edu.sg.appfiles.mobile_ewaste.network.EwasteApi
import iss.nus.edu.sg.appfiles.mobile_ewaste.network.model.LoginRequest
import iss.nus.edu.sg.appfiles.mobile_ewaste.network.model.LoginResponse
import iss.nus.edu.sg.appfiles.mobile_ewaste.network.model.RegisterRequest
import iss.nus.edu.sg.appfiles.mobile_ewaste.network.model.RegisterResponse

class AuthRepository(private val ewasteApi: EwasteApi) {
    suspend fun login(request: LoginRequest): LoginResponse = ewasteApi.login(request)

    suspend fun register(request: RegisterRequest): RegisterResponse = ewasteApi.register(request)
}
