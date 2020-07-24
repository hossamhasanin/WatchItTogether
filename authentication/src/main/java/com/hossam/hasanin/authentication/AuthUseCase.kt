package com.hossam.hasanin.authentication

import android.util.Log
import com.hossam.hasanin.authentication.login.LoginViewState
import com.hossam.hasanin.authentication.signup.SignupViewState
import com.hossam.hasanin.base.models.User
import com.hossam.hasanin.base.repositories.AuthRepository
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.lang.Exception
import javax.inject.Inject

class AuthUseCase @Inject constructor(private val repo: AuthRepository) {
    fun login(viewState: LoginViewState): Observable<LoginViewState>{
        return repo.login(viewState.email , viewState.pass).materialize().map {
            it.value?.let {
                return@map viewState.copy(
                    logged = true,
                    logging = false
                )
            }
            it.error?.let {
                return@map viewState.copy(
                    logging = false,
                    logged = false,
                    error = it as Exception
                )
            }
            return@map viewState.copy(
                error = null,
                logged = false,
                logging = false
            )
        }.toObservable().subscribeOn(Schedulers.io())
    }

    fun cashCurrentUser(viewState: AuthViewState): Observable<AuthViewState>{
        Log.v("lolo" , User.current.toString())
        return repo.cashCurrentUser(User.current!!).materialize<Unit>().map {
            viewState
        }.toObservable().subscribeOn(Schedulers.io())
    }

    fun signup(viewState: SignupViewState): Observable<SignupViewState>{
        return repo.signup(viewState.user!! , viewState.pass!!).materialize().map {
            it.value?.let {
                return@map viewState.copy(
                    error = null,
                    logged = true,
                    logging = false
                )
            }
            it.error?.let {
                return@map viewState.copy(
                    logging = false,
                    logged = false,
                    error = it as Exception
                )
            }
            return@map viewState.copy(
                error = null,
                logged = false,
                logging = false
            )
        }.toObservable().subscribeOn(Schedulers.io())
    }
}