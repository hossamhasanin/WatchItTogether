package com.hossam.hasanin.base.repositories

import com.google.firebase.auth.AuthResult
import com.hossam.hasanin.base.models.User
import io.reactivex.Completable
import io.reactivex.Maybe

interface AuthRepository {
    fun login(email: String , pass: String): Maybe<AuthResult>
    fun signup(user: User, pass: String): Maybe<AuthResult>
    fun cashCurrentUser(user: User): Completable
    fun removeCashCurrentUser(user: User): Completable
    fun getCashedCurrentUser(): Maybe<User>
}