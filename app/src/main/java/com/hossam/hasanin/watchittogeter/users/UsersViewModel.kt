package com.hossam.hasanin.watchittogeter.users

import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.hossam.hasanin.watchittogeter.models.WatchRoom
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

class UsersViewModel @ViewModelInject constructor(private val usersUseCases: UsersUseCases) : ViewModel() {
    private val _viewState = BehaviorSubject.create<UsersViewState>().apply {
        onNext(UsersViewState(users = listOf() , loading = true , loadingMore = false , error = null , refresh = false , creatingRoom = false , roomCreated = false))
    }

    val cashedList = mutableListOf<UserWrapper>()

    private val compositeDisposable = CompositeDisposable()

    fun viewState(): Observable<UsersViewState> = _viewState
    fun viewStateValue(): UsersViewState = _viewState.value!!

    private val _loadingUsers = PublishSubject.create<Unit>()
    private val _creatingRoom = PublishSubject.create<WatchRoom>()

    init {
        bindUi()

        _loadingUsers.onNext(Unit)
    }

    private fun bindUi(){
        val disposable = Observable.merge(loadUsers() , createRoom()).doOnNext {
            postViewStateValue(it)
        }.observeOn(AndroidSchedulers.mainThread()).subscribe({}, {
            it.printStackTrace()
        })

        compositeDisposable.add(disposable)
    }

    private fun loadUsers(): Observable<UsersViewState>{
        return _loadingUsers.switchMap { usersUseCases.getUsers(viewStateValue()) }
    }

    private fun createRoom(): Observable<UsersViewState>{
        return _creatingRoom.switchMap { usersUseCases.createRoom(viewStateValue() , it) }
    }

    fun loadMore(){
        if (viewStateValue().loadingMore || viewStateValue().refresh) return
        val list = viewStateValue().users.toMutableList()
        list.add(UserWrapper(null , UserWrapper.LOADING))
        postViewStateValue(
            viewStateValue().copy(users = list , loadingMore = true)
        )
    }

    fun createRoom(watchRoom: WatchRoom){
        if (viewStateValue().creatingRoom) return
        postViewStateValue(viewStateValue().copy(creatingRoom = true))
        _creatingRoom.onNext(watchRoom)
    }

    fun clearCreatingRoomStates(){
        postViewStateValue(viewStateValue().copy(roomCreated = false))
    }

    private fun postViewStateValue(viewState: UsersViewState){
        _viewState.onNext(viewState)
    }

}