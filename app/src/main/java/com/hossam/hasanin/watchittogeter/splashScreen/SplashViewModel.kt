package com.hossam.hasanin.watchittogeter.splashScreen

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.hossam.hasanin.base.models.User
import com.hossam.hasanin.base.repositories.AuthRepository
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

class SplashViewModel @ViewModelInject constructor(private val mAuth: FirebaseAuth , private val repo: AuthRepository): ViewModel() {
    val compositeDisposable = CompositeDisposable()

    fun checkIfLoggedIn(loggedIn: (Boolean)-> Unit){
        if (mAuth.currentUser != null){
            Log.v("lolo", "get cash")
            _gettingCashedUser.onNext(Unit)
            loggedIn(true)
        } else {
            loggedIn(false)
        }
    }

    private val _gettingCashedUser = PublishSubject.create<Unit>()

    init{
        val dis = _getCashedUser().doOnNext {
            User.current = it
            Log.v("lolo" , it.toString())
        }.observeOn(AndroidSchedulers.mainThread()).subscribe(){}

        compositeDisposable.add(dis)

    }

    private fun _getCashedUser(): Observable<User>{
        return _gettingCashedUser.switchMap { repo.getCashedCurrentUser().materialize().map {
            it.value?.let {
                return@map it
            }
            it.error?.let {
                throw it
            }
            return@map User(roomId = null)
        }.toObservable().subscribeOn(Schedulers.io()) }
    }

}