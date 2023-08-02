package com.brianchuquiruna.evitastore.domain

import com.brianchuquiruna.evitastore.data.UserRepository
import com.brianchuquiruna.evitastore.data.model.toNetwork
import com.brianchuquiruna.evitastore.data.network.response.LoginResponse
import com.brianchuquiruna.evitastore.domain.model.User
import javax.inject.Inject

class LoginUserUseCase @Inject constructor(
    private val repository : UserRepository
){
    suspend operator fun invoke(user:User): LoginResponse =
        repository.login(user.toNetwork())
}