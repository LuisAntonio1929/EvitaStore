package com.brianchuquiruna.evitastore.data.network.response

sealed class LoginResponse {
    object Error : LoginResponse()
    object Success : LoginResponse()
}