package com.brianchuquiruna.evitastore.ui.signup

data class SignUpViewState(
    val isLoading: Boolean = false,
    val isValidEmail: Boolean = true,
    val isValidPassword: Boolean = true,
){
    fun userValidated() = isValidEmail && isValidPassword
}
