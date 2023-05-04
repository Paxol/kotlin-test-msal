package it.paxol.msaltest.presentation.login

sealed class LoginIntent {
    object LoginClickedIntent : LoginIntent()
}