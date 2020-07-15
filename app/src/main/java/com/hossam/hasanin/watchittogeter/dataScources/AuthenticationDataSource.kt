package com.hossam.hasanin.watchittogeter.dataScources

import com.google.firebase.auth.AuthResult
import com.hossam.hasanin.watchittogeter.models.User
import io.reactivex.Completable
import io.reactivex.Maybe

interface AuthenticationDataSource {
    fun login(email: String , pass: String): Maybe<AuthResult>
    fun signup(user: User , pass: String): Maybe<AuthResult>
}