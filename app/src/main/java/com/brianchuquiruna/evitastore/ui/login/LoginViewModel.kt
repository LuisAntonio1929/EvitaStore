package com.brianchuquiruna.evitastore.ui.login

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brianchuquiruna.evitastore.core.Event
import com.brianchuquiruna.evitastore.data.network.response.LoginResponse
import com.brianchuquiruna.evitastore.domain.LoginUserUseCase
import com.brianchuquiruna.evitastore.domain.SignUpUserUseCase
import com.brianchuquiruna.evitastore.domain.model.User
import com.brianchuquiruna.evitastore.domain.model.toDomain
import com.brianchuquiruna.evitastore.ui.login.model.UserLogin
import com.brianchuquiruna.evitastore.ui.signup.model.UserSignUp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUserUseCase : LoginUserUseCase
) : ViewModel(){

    private companion object {
        const val MIN_PASSWORD_LENGTH = 6
    }
    private val _navigateToSignUp = MutableLiveData<Event<Boolean>>()
    val navigateToSignUp: LiveData<Event<Boolean>>
        get() = _navigateToSignUp

    private val _navigateToDetails = MutableLiveData<Event<Boolean>>()
    val navigateToDetails: LiveData<Event<Boolean>>
        get() = _navigateToDetails

    private val _navigateToHome = MutableLiveData<Event<Boolean>>()
    val navigateToHome: LiveData<Event<Boolean>>
        get() = _navigateToHome

    private val _viewState = MutableStateFlow(LoginViewState())
    val viewState: StateFlow<LoginViewState>
        get() = _viewState

    private var _showErrorDialog = MutableLiveData(UserLogin())
    val showErrorDialog: LiveData<UserLogin>
        get() = _showErrorDialog

    fun onLoginSelected(userLogin: UserLogin) {
        if (isValidEmail(userLogin.email) && isValidPassword(userLogin.password)) {
            loginUser(userLogin)
        } else {
            onFieldsChanged(userLogin)
        }
    }

    private fun loginUser(userLogin: UserLogin) {
        viewModelScope.launch {
            _viewState.value = LoginViewState(isLoading = true)
            when (loginUserUseCase(userLogin.toDomain())) {
                LoginResponse.Error -> {
                    _showErrorDialog.value =
                        UserLogin(email = userLogin.email, password = userLogin.password, showErrorDialog = true)
                    _viewState.value = LoginViewState(isLoading = false)
                }
                LoginResponse.Success -> {
                    _navigateToHome.value = Event(true)
                }
            }
            _viewState.value = LoginViewState(isLoading = false)
        }
    }

    fun onFieldsChanged(userLogin: UserLogin) {
        _viewState.value = LoginViewState(
            isValidEmail = isValidEmail(userLogin.email),
            isValidPassword = isValidPassword(userLogin.password)
        )
    }

    fun onSignUpSelected() {
        _navigateToSignUp.value = Event(true)
    }

    private fun isValidEmail(email: String) =
        Patterns.EMAIL_ADDRESS.matcher(email).matches() || email.isEmpty()

    private fun isValidPassword(password: String): Boolean =
        password.length >= MIN_PASSWORD_LENGTH || password.isEmpty()
}