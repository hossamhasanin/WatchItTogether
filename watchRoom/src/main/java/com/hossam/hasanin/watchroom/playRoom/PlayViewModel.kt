package com.hossam.hasanin.watchroom.playRoom

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.hossam.hasanin.base.models.User
import com.hossam.hasanin.base.models.UserState
import com.hossam.hasanin.base.models.WatchRoom
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

class PlayViewModel @ViewModelInject constructor(private val useCase: PlayUseCase): ViewModel() {
    private val _viewState = BehaviorSubject.create<PlayViewState>().apply {
        onNext(
            PlayViewState(listOf() , true , null , WatchRoom.PREPARING , null , false)
        )
    }

    var cashedRoomId: String? = null
    private val compositeDisposable = CompositeDisposable()

    var currentUserState = BehaviorSubject.create<UserState>()


    fun viewState(): Observable<PlayViewState> = _viewState
    fun viewStateValue(): PlayViewState = _viewState.value!!
    fun currentUserStateValue(): UserState = currentUserState.value!!

    private val _gettingUsers = PublishSubject.create<String>()

    init {
        val userStateListener = currentUserState.switchMap { useCase.refreshUserState(it , cashedRoomId!!) }.subscribe(){}
        val dis = _getUsers().doOnNext { postViewStateValue(it) }.observeOn(AndroidSchedulers.mainThread()).subscribe(){}

        compositeDisposable.addAll(userStateListener , dis)
    }


    private fun _getUsers(): Observable<PlayViewState> {
        return _gettingUsers.switchMap { useCase.usersStateListener(viewStateValue() , it) }
    }

    fun getUsers(roomId: String , isLeader: Boolean){
        if (viewStateValue().users.isNotEmpty()) return
        cashedRoomId = roomId
        Log.v("soso" , "getUsers here $roomId")
        currentUserState.onNext(UserState(id = User.current?.id!! , gender = User.current?.gender , name = User.current?.name!!
            , videoPosition = 0 , state = UserState.PAUSE , leader = isLeader))
        _gettingUsers.onNext(roomId)
    }

    fun updateUserState(state: Int , videoPosition: Long?){
        currentUserState.onNext(currentUserStateValue().copy(state = state , videoPosition = videoPosition?: currentUserStateValue().videoPosition))
    }

    fun viewUserStateRec(open: Boolean){
        postViewStateValue(viewStateValue().copy(showUsersState = open))
    }

    private fun postViewStateValue(viewState: PlayViewState){
        _viewState.onNext(viewState)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }


}