package com.brianchuquiruna.evitastore.ui.signup

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brianchuquiruna.evitastore.core.Event
import com.brianchuquiruna.evitastore.domain.SignUpUserUseCase
import com.brianchuquiruna.evitastore.domain.model.toDomain
import com.brianchuquiruna.evitastore.ui.signup.model.UserSignUp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    val signUpUserUseCase: SignUpUserUseCase
) : ViewModel(){

    private companion object {
        const val MIN_PASSWORD_LENGTH = 6
    }

    private val _navigateToHome = MutableLiveData<Event<Boolean>>()
    val navigateToHome: LiveData<Event<Boolean>>
        get() = _navigateToHome

//    private val _navigateToVerifyEmail = MutableLiveData<Event<Boolean>>()
//    val navigateToVerifyEmail: LiveData<Event<Boolean>>
//        get() = _navigateToVerifyEmail

    private val _viewState = MutableStateFlow(SignUpViewState())
    val viewState: StateFlow<SignUpViewState>
        get() = _viewState

    private var _showErrorDialog = MutableLiveData(false)
    val showErrorDialog: LiveData<Boolean>
        get() = _showErrorDialog

    fun onSignUpSelected(userSignUp: UserSignUp) {
        val viewState = userSignUp.toSignUpViewState()
        if (viewState.userValidated() && userSignUp.isNotEmpty()) {
            signUpUser(userSignUp)
        } else {
            onFieldsChanged(userSignUp)
        }
    }

    private fun signUpUser(userSignUp: UserSignUp) {
        viewModelScope.launch {
            _viewState.value = SignUpViewState(isLoading = true)
            val accountCreated = signUpUserUseCase(userSignUp.toDomain())
            if (accountCreated) {
                //_navigateToHome.value = Event(true)
                _navigateToHome.value = Event(true)
            } else {
                _showErrorDialog.value = true
            }
            _viewState.value = SignUpViewState(isLoading = false)
        }
    }

    fun onFieldsChanged(userSignUp: UserSignUp) {
        _viewState.value = userSignUp.toSignUpViewState()
    }

    fun onHomeSelected() {
        _navigateToHome.value = Event(true)
    }

    private fun isValidOrEmptyEmail(email: String) =
        Patterns.EMAIL_ADDRESS.matcher(email).matches() || email.isEmpty()

    private fun isValidOrEmptyPassword(password: String, passwordConfirmation: String): Boolean =
        (password.length >= MIN_PASSWORD_LENGTH && password == passwordConfirmation) || password.isEmpty() || passwordConfirmation.isEmpty()

    private fun UserSignUp.toSignUpViewState(): SignUpViewState {
        return SignUpViewState(
            isValidEmail = isValidOrEmptyEmail(email),
            isValidPassword = isValidOrEmptyPassword(password, passwordConfirmation)
        )
    }
}