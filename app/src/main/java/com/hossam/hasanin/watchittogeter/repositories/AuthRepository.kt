package com.hossam.hasanin.watchittogeter.repositories

import com.google.firebase.auth.AuthResult
import com.hossam.hasanin.watchittogeter.models.User
import io.reactivex.Maybe

interface AuthRepository {
    fun login(email: String , pass: String): Maybe<AuthResult>
    fun signup(user: User , pass: String): Maybe<AuthResult>
}