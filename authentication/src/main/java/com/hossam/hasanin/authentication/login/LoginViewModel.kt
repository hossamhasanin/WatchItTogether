package com.hossam.hasanin.authentication.login

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.hossam.hasanin.authentication.AuthUseCase
import com.hossam.hasanin.watchittogeter.users.UserWrapper
import com.hossam.hasanin.watchittogeter.users.UsersViewState
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

class LoginViewModel @ViewModelInject constructor(private val useCase: AuthUseCase) : ViewModel() {
    private val _viewState = BehaviorSubject.create<LoginViewState>().apply {
        onNext(
            LoginViewState("" , "" , null , false , false)
        )
    }

    private val compositeDisposable = CompositeDisposable()

    fun viewState(): Observable<LoginViewState> = _viewState
    fun viewStateValue(): LoginViewState = _viewState.value!!

    private val _loggingIn = PublishSubject.create<Unit>()

    init {
        bindUi()
    }

    private fun bindUi(){
        val dis = _login().doOnNext { postViewStateValue(it) }
            .observeOn(AndroidSchedulers.mainThread()).subscribe(){}
        compositeDisposable.add(dis)
    }

    private fun _login(): Observable<LoginViewState>{
        return _loggingIn.switchMap { useCase.login(viewStateValue()) }
    }

    fun login(email: String , pass: String){
        if (viewStateValue().logging || viewStateValue().logged) return
        postViewStateValue(viewStateValue().copy(email= email , pass = pass , logging = true , logged = false))
        _loggingIn.onNext(Unit)
    }

    private fun postViewStateValue(viewState: LoginViewState){
        _viewState.onNext(viewState)
    }
}