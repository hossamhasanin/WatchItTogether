package com.hossam.hasanin.watchroom.groupRoom

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.hossam.hasanin.watchittogeter.models.WatchRoom
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

class GroupViewModel @ViewModelInject constructor(private val useCase: GroupUseCase) : ViewModel() {
    private val _viewState = BehaviorSubject.create<GroupViewState>().apply {
        onNext(
            GroupViewState(listOf() , true , null , WatchRoom.READY)
        )
    }

    private val compositeDisposable = CompositeDisposable()

    fun viewState(): Observable<GroupViewState> = _viewState
    fun viewStateValue(): GroupViewState = _viewState.value!!

    private val _gettingUsers = PublishSubject.create<String>()

    init {
        bindUi()
    }

    private fun bindUi(){
        val dis = _getUsers().doOnNext { postViewStateValue(it) }
            .observeOn(AndroidSchedulers.mainThread()).subscribe(){}
        compositeDisposable.add(dis)
    }

    private fun _getUsers(): Observable<GroupViewState> {
        return _gettingUsers.switchMap { useCase.usersListener(viewStateValue() , it) }
    }

    private fun postViewStateValue(viewState: GroupViewState){
        _viewState.onNext(viewState)
    }
}