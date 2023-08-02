package com.brianchuquiruna.evitastore.ui.login

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.brianchuquiruna.evitastore.R
import com.brianchuquiruna.evitastore.core.dialog.DialogFragmentLauncher
import com.brianchuquiruna.evitastore.core.dialog.ErrorDialog
import com.brianchuquiruna.evitastore.core.dialog.LoginSuccessDialog
import com.brianchuquiruna.evitastore.core.ex.dismissKeyboard
import com.brianchuquiruna.evitastore.core.ex.loseFocusAfterAction
import com.brianchuquiruna.evitastore.core.ex.onTextChanged
import com.brianchuquiruna.evitastore.core.ex.show
import com.brianchuquiruna.evitastore.databinding.ActivityLoginBinding
import com.brianchuquiruna.evitastore.ui.login.model.UserLogin
import com.brianchuquiruna.evitastore.ui.products.ProductActivity
import com.brianchuquiruna.evitastore.ui.signup.SignUpActivity
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    companion object {
        fun create(context: Context): Intent =
            Intent(context, LoginActivity::class.java)
    }

    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel : LoginViewModel by viewModels()

    @Inject
    lateinit var dialogLauncher: DialogFragmentLauncher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
//        FirebaseAnalitycs
//        val bundle = Bundle()
//        bundle.putString("message", "Integracion de firebase completa")
//        firebaseAnalytics.logEvent("InitScreen", bundle)
    }
    private fun initUI() {
        initListeners()
        initObservers()
    }

    private fun initListeners() {
        binding.etTextEmailAddress.loseFocusAfterAction(EditorInfo.IME_ACTION_NEXT)
        binding.etTextEmailAddress.setOnFocusChangeListener { _, hasFocus -> onFieldChanged(hasFocus) }
        binding.etTextEmailAddress.onTextChanged { onFieldChanged() }

        binding.etTextPassword.loseFocusAfterAction(EditorInfo.IME_ACTION_DONE)
        binding.etTextPassword.setOnFocusChangeListener { _, hasFocus -> onFieldChanged(hasFocus) }
        binding.etTextPassword.onTextChanged { onFieldChanged() }

        binding.btnSignUp.setOnClickListener {
            loginViewModel.onSignUpSelected()
        }

        binding.btnSignIn.setOnClickListener {
            it.dismissKeyboard()
            loginViewModel.onLoginSelected(
                UserLogin(
                    email = binding.etTextEmailAddress.text.toString(),
                    password = binding.etTextPassword.text.toString()
                )
            )
        }
    }

    private fun initObservers() {
        loginViewModel.navigateToDetails.observe(this) {
            it.getContentIfNotHandled()?.let {
                goToDetail()
            }
        }

        loginViewModel.navigateToHome.observe(this) {
            it.getContentIfNotHandled()?.let {
                goToHome()
            }
        }

        loginViewModel.navigateToSignUp.observe(this) {
            it.getContentIfNotHandled()?.let {
                goToSignUp()
            }
        }

        loginViewModel.showErrorDialog.observe(this) { userLogin ->
            if (userLogin.showErrorDialog) showErrorDialog(userLogin)
        }

        lifecycleScope.launchWhenStarted {
            loginViewModel.viewState.collect { viewState ->
                updateUI(viewState)
            }
        }
    }

    private fun updateUI(viewState: LoginViewState) {
        with(binding) {
            pbLoading.isVisible = viewState.isLoading
            tilEmail.error =
                if (viewState.isValidEmail) null else getString(R.string.login_error_mail)
            tilPassword.error =
                if (viewState.isValidPassword) null else getString(R.string.login_error_password)
        }
    }

    private fun onFieldChanged(hasFocus: Boolean = false) {
        if (!hasFocus) {
            loginViewModel.onFieldsChanged(
                UserLogin(
                    email = binding.etTextEmailAddress.text.toString(),
                    password = binding.etTextPassword.text.toString()
                )
            )
        }
    }

    private fun showErrorDialog(userLogin: UserLogin) {
        ErrorDialog.create(
            title = getString(R.string.login_error_dialog_title),
            description = getString(R.string.login_error_dialog_body),
            negativeAction = ErrorDialog.Action(getString(R.string.login_error_dialog_negative_action)) {
                it.dismiss()
            },
            positiveAction = ErrorDialog.Action(getString(R.string.login_error_dialog_positive_action)) {
                loginViewModel.onLoginSelected(
                    UserLogin(
                        email = binding.etTextEmailAddress.text.toString(),
                        password = binding.etTextPassword.text.toString()
                    )
                )
                it.dismiss()
            }
        ).show(dialogLauncher, this)
    }

    private fun goToHome() {
        startActivity(ProductActivity.create(this))
    }

    private fun goToDetail() {
        LoginSuccessDialog.create().show(dialogLauncher, this)
    }

    private fun goToSignUp() {
        startActivity(SignUpActivity.create(this))
    }
}