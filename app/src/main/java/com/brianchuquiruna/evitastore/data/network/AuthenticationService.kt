package com.brianchuquiruna.evitastore.data.network

import com.brianchuquiruna.evitastore.data.network.response.LoginResponse
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthenticationService @Inject constructor(private val firebase: FirebaseClient) {


    suspend fun login(email: String, password: String): LoginResponse = runCatching {
        firebase.auth.signInWithEmailAndPassword(email, password).await()
    }.toLoginResponse()

    suspend fun createAccount(email: String, password: String): AuthResult? {
        return firebase.auth.createUserWithEmailAndPassword(email, password).await()
    }

    private fun Result<AuthResult>.toLoginResponse() = when (val result = getOrNull()) {
        null -> LoginResponse.Error
        else -> {
            val userId = result.user
            checkNotNull(userId)
            LoginResponse.Success
        }
    }


}