package it.paxol.msaltest.presentation.login

data class LoginViewState(
    val isLoading: Boolean = false,
    val error: String? = null,
)
