package com.hossam.hasanin.authentication.login

import java.lang.Exception

data class LoginViewState(
    val email: String,
    val pass: String,
    val error: Exception?,
    val logging: Boolean,
    val logged: Boolean
)