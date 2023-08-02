package com.brianchuquiruna.evitastore.ui.signup.model

data class UserSignUp(
    val email: String,
    val password: String,
    val passwordConfirmation: String
) {
    fun isNotEmpty() = email.isNotEmpty() && password.isNotEmpty() && passwordConfirmation.isNotEmpty()
}