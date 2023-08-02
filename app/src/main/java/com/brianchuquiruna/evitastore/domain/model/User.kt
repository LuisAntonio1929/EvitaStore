package com.brianchuquiruna.evitastore.domain.model

import com.brianchuquiruna.evitastore.ui.login.model.UserLogin
import com.brianchuquiruna.evitastore.ui.signup.model.UserSignUp

data class User(
    val email:String,
    val password:String
)

fun UserSignUp.toDomain() =User(email, password)

fun UserLogin.toDomain() =User(email, password)