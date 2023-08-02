package com.brianchuquiruna.evitastore.domain

import com.brianchuquiruna.evitastore.data.UserRepository
import com.brianchuquiruna.evitastore.data.model.toNetwork
import com.brianchuquiruna.evitastore.domain.model.User
import javax.inject.Inject

class SignUpUserUseCase @Inject constructor(
    private val repository : UserRepository
){
    suspend operator fun invoke(user: User): Boolean {
        return repository.createAccount(user.toNetwork()) != null
    }
}