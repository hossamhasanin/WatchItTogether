package com.hossam.hasanin.watchittogeter.repositories

import com.google.firebase.auth.AuthResult
import com.hossam.hasanin.watchittogeter.dataScources.AuthenticationDataSource
import com.hossam.hasanin.watchittogeter.models.User
import io.reactivex.Maybe
import javax.inject.Inject

class AuthRepositoryImp @Inject constructor(private val dataSource: AuthenticationDataSource): AuthRepository {
    override fun login(email: String, pass: String): Maybe<AuthResult> {
        return dataSource.login(email, pass)
    }

    override fun signup(user: User , pass: String): Maybe<AuthResult> {
        return dataSource.signup(user , pass)
    }
}