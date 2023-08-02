package com.brianchuquiruna.evitastore.ui.signup

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
import com.brianchuquiruna.evitastore.core.ex.dismissKeyboard
import com.brianchuquiruna.evitastore.core.ex.loseFocusAfterAction
import com.brianchuquiruna.evitastore.core.ex.onTextChanged
import com.brianchuquiruna.evitastore.core.ex.show
import com.brianchuquiruna.evitastore.databinding.ActivitySignUpBinding
import com.brianchuquiruna.evitastore.ui.login.LoginActivity
import com.brianchuquiruna.evitastore.ui.products.ProductActivity
import com.brianchuquiruna.evitastore.ui.signup.model.UserSignUp
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SignUpActivity : AppCompatActivity() {

    companion object {
        fun create(context: Context): Intent =
            Intent(context, SignUpActivity::class.java)
    }

    private lateinit var binding: ActivitySignUpBinding
    private val signUpViewModel : SignUpViewModel by viewModels()

    @Inject
    lateinit var dialogLauncher: DialogFragmentLauncher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
    }

    private fun initUI() {
        initListeners()
        initObservers()
    }


    private fun initListeners() {

        binding.etTextEmailAddress.loseFocusAfterAction(EditorInfo.IME_ACTION_NEXT)
        binding.etTextEmailAddress.setOnFocusChangeListener { _, hasFocus -> onFieldChanged(hasFocus) }
        binding.etTextEmailAddress.onTextChanged { onFieldChanged() }


        binding.etTextPassword.loseFocusAfterAction(EditorInfo.IME_ACTION_NEXT)
        binding.etTextPassword.setOnFocusChangeListener { _, hasFocus -> onFieldChanged(hasFocus) }
        binding.etTextPassword.onTextChanged { onFieldChanged() }

        binding.etRepeteadTextPassword.loseFocusAfterAction(EditorInfo.IME_ACTION_DONE)
        binding.etRepeteadTextPassword.setOnFocusChangeListener { _, hasFocus -> onFieldChanged(hasFocus) }
        binding.etRepeteadTextPassword.onTextChanged { onFieldChanged() }


        with(binding) {
            btnSignUp.setOnClickListener {
                it.dismissKeyboard()
                signUpViewModel.onSignUpSelected(
                    UserSignUp(
                        email = binding.etTextEmailAddress.text.toString(),
                        password = binding.etTextPassword.text.toString(),
                        passwordConfirmation = binding.etRepeteadTextPassword.text.toString()
                    )
                )
            }
        }
    }

    private fun initObservers() {

        signUpViewModel.navigateToHome.observe(this) {
            it.getContentIfNotHandled()?.let {
                goToHome()
            }
        }

        lifecycleScope.launchWhenStarted {
            signUpViewModel.viewState.collect { viewState ->
                updateUI(viewState)
            }
        }

        signUpViewModel.showErrorDialog.observe(this) { showError ->
            if (showError) showErrorDialog()
        }
    }

    private fun showErrorDialog() {
        ErrorDialog.create(
            title = getString(R.string.signup_error_dialog_title),
            description = getString(R.string.signup_error_dialog_body),
            positiveAction = ErrorDialog.Action(getString(R.string.signup_error_dialog_positive_action)) {
                it.dismiss()
            }
        ).show(dialogLauncher, this)
    }

    private fun updateUI(viewState: SignUpViewState) {
        with(binding) {
            pbLoading.isVisible = viewState.isLoading
            binding.tilEmail.error =
                if (viewState.isValidEmail) null else getString(R.string.signup_error_mail)
            binding.tilPassword.error =
                if (viewState.isValidPassword) null else getString(R.string.signup_error_password)
            binding.tilRepeatPassword.error =
                if (viewState.isValidPassword) null else getString(R.string.signup_error_password)
        }
    }

    private fun onFieldChanged(hasFocus: Boolean = false) {
        if (!hasFocus) {
            signUpViewModel.onFieldsChanged(
                UserSignUp(
                    email = binding.etTextEmailAddress.text.toString(),
                    password = binding.etTextPassword.text.toString(),
                    passwordConfirmation = binding.etRepeteadTextPassword.text.toString()
                )
            )
        }
    }

    private fun goToHome() {
        startActivity(ProductActivity.create(this))
    }
}