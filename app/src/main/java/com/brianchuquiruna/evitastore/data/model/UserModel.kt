package com.brianchuquiruna.evitastore.data.model

import com.brianchuquiruna.evitastore.domain.model.User
import com.brianchuquiruna.evitastore.ui.signup.model.UserSignUp
import com.google.gson.annotations.SerializedName

data class UserModel(
    val email:String,
    val password:String
)

fun User.toNetwork() = UserModel(email, password)