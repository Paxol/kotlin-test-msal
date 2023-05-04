package it.paxol.msaltest.presentation.login

import android.util.Log
import androidx.lifecycle.ViewModel
import com.microsoft.identity.client.AuthenticationCallback
import com.microsoft.identity.client.IAuthenticationResult
import com.microsoft.identity.client.exception.MsalException
import it.paxol.msaltest.data.Authentication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LoginViewModel() : ViewModel() {
    private val _state = MutableStateFlow(LoginViewState())
    val state: StateFlow<LoginViewState> = _state

    var auth: Authentication? = null

    fun onIntent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.LoginClickedIntent -> handleLogin()
        }
    }

    private fun handleLogin() {
        _state.value = _state.value.copy(isLoading = true, error = null)

        auth!!.signIn(object : AuthenticationCallback{
            override fun onSuccess(authenticationResult: IAuthenticationResult?) {
                Log.i("LoginViewModel", "LoginViewModel callback")

                _state.value = _state.value.copy(isLoading = false, error = null)
            }

            override fun onCancel() {
                Log.i("LoginViewModel", "LoginViewModel onCancel")

                _state.value = _state.value.copy(isLoading = false, error = null)
            }

            override fun onError(exception: MsalException?) {
                Log.i("LoginViewModel", "LoginViewModel onError")

                val msg = exception?.message

                _state.value = _state.value.copy(isLoading = false, error = msg)
            }
        })
    }
}
