package com.hossam.hasanin.base.dataScources

import com.google.firebase.auth.AuthResult
import com.hossam.hasanin.base.models.User
import io.reactivex.Maybe

interface AuthenticationDataSource {
    fun login(email: String , pass: String): Maybe<AuthResult>
    fun signup(user: User, pass: String): Maybe<AuthResult>
}