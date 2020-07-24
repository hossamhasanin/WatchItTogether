package com.hossam.hasanin.base.repositories

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.hossam.hasanin.base.dataScources.AuthenticationDataSource
import com.hossam.hasanin.base.dataScources.UserCashDataSource
import com.hossam.hasanin.base.models.User
import com.hossam.hasanin.base.repositories.AuthRepository
import io.reactivex.Completable
import io.reactivex.Maybe
import javax.inject.Inject

class AuthRepositoryImp @Inject constructor(private val dataSource: AuthenticationDataSource
                                            , private val userCashDataSource: UserCashDataSource
                                            , private val mAuth: FirebaseAuth): AuthRepository {
    override fun login(email: String, pass: String): Maybe<AuthResult> {
        return dataSource.login(email, pass)
    }

    override fun signup(user: User, pass: String): Maybe<AuthResult> {
        return dataSource.signup(user , pass)
    }

    override fun cashCurrentUser(user: User): Completable {
        return userCashDataSource.setCurrentUser(user)
    }

    override fun removeCashCurrentUser(user: User): Completable {
        return userCashDataSource.deleteCurrentUser(user)
    }

    override fun getCashedCurrentUser(): Maybe<User> {
        return userCashDataSource.getCurrentUser(mAuth.currentUser!!.uid)
    }
}