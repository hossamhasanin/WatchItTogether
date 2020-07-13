package com.hossam.hasanin.watchittogeter.users

import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.hossam.hasanin.watchittogeter.models.User
import com.hossam.hasanin.watchittogeter.models.WatchRoom
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

class UsersViewModel @ViewModelInject constructor(private val usersUseCases: UsersUseCases , private val mAuth: FirebaseAuth) : ViewModel() {
    private val _viewState = BehaviorSubject.create<UsersViewState>().apply {
        onNext(UsersViewState(users = listOf() , loading = true , loadingMore = false , error = null
            , refresh = false , creatingRoom = false , roomCreated = false , addingContact = false
            , createRoomError = null , addContactError = null))
    }

    val cashedList = mutableListOf<UserWrapper>()

    val currentUser = mAuth.currentUser!!

    private val compositeDisposable = CompositeDisposable()

    fun viewState(): Observable<UsersViewState> = _viewState
    fun viewStateValue(): UsersViewState = _viewState.value!!

    private val _loadingUsers = PublishSubject.create<Unit>()
    private val _creatingRoom = PublishSubject.create<WatchRoom>()
    private val _addingContact = PublishSubject.create<String>()

    init {
        bindUi()

        _loadingUsers.onNext(Unit)
    }

    private fun bindUi(){
        val disposable = Observable.merge(loadUsers() , createRoom() , addContact()).doOnNext {
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
            .switchMap { usersUseCases.updateUserWatchRoom(it) }
    }

    private fun addContact(): Observable<UsersViewState>{
        return _addingContact.switchMap { usersUseCases.addContact(viewStateValue() , it) }
    }

    fun loadMore(){
        if (viewStateValue().loadingMore || viewStateValue().refresh) return
        val list = viewStateValue().users.toMutableList()
        list.add(UserWrapper(null , UserWrapper.LOADING))
        postViewStateValue(
            viewStateValue().copy(users = list , loadingMore = true)
        )
        _loadingUsers.onNext(Unit)
    }

    fun createRoom(watchRoom: WatchRoom){
        if (viewStateValue().creatingRoom) return
        postViewStateValue(viewStateValue().copy(creatingRoom = true))
        _creatingRoom.onNext(watchRoom)
    }

    fun addContact(query: String){
        if (viewStateValue().addingContact) return
        postViewStateValue(viewStateValue().copy(addingContact = true))
        _addingContact.onNext(query)
    }

    fun clearCreatingRoomStates(){
        postViewStateValue(viewStateValue().copy(roomCreated = false))
    }

    private fun postViewStateValue(viewState: UsersViewState){
        _viewState.onNext(viewState)
    }

}