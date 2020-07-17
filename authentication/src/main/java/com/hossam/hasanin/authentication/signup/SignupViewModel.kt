package com.hossam.hasanin.authentication.signup

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.hossam.hasanin.authentication.AuthUseCase
import com.hossam.hasanin.watchittogeter.models.User
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

class SignupViewModel @ViewModelInject constructor(private val useCase: AuthUseCase) : ViewModel() {
    private val _viewState = BehaviorSubject.create<SignupViewState>().apply {
        onNext(
            SignupViewState(null , "" , null , false , false)
        )
    }

    private val compositeDisposable = CompositeDisposable()

    fun viewState(): Observable<SignupViewState> = _viewState
    fun viewStateValue(): SignupViewState = _viewState.value!!

    private val _signingUp = PublishSubject.create<Unit>()

    init {
        bindUi()
    }

    private fun bindUi(){
        val dis = _signup().doOnNext { postViewStateValue(it) }
            .observeOn(AndroidSchedulers.mainThread()).subscribe(){}
        compositeDisposable.add(dis)
    }

    private fun _signup(): Observable<SignupViewState> {
        return _signingUp.switchMap { useCase.signup(viewStateValue()) }
    }

    fun signup(user: User, pass: String){
        if (viewStateValue().logging || viewStateValue().logged) return
        postViewStateValue(viewStateValue().copy(user= user , pass = pass , logging = true , logged = false))
        _signingUp.onNext(Unit)
    }

    private fun postViewStateValue(viewState: SignupViewState){
        _viewState.onNext(viewState)
    }}