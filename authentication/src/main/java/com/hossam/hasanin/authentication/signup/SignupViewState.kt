package com.hossam.hasanin.authentication.signup

import com.hossam.hasanin.authentication.AuthViewState
import com.hossam.hasanin.base.models.User
import java.lang.Exception

data class SignupViewState(
    val user: User?,
    val pass: String?,
    val error: Exception?,
    val logging: Boolean,
    val logged: Boolean
): AuthViewState