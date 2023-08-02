package com.brianchuquiruna.evitastore.data

import com.brianchuquiruna.evitastore.data.model.UserModel
import com.brianchuquiruna.evitastore.data.network.AuthenticationService
import com.brianchuquiruna.evitastore.data.network.response.LoginResponse
import com.google.firebase.auth.AuthResult
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val authenticationService: AuthenticationService,
){

    suspend fun createAccount(userModel:UserModel): AuthResult?{
        return authenticationService.createAccount(userModel.email, userModel.password)
    }

    suspend fun login(userModel:UserModel): LoginResponse {
        return authenticationService.login(userModel.email, userModel.password)
    }
}