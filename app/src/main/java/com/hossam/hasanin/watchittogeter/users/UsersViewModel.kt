package com.hossam.hasanin.watchittogeter.users

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.hossam.hasanin.base.models.WatchRoom
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

class UsersViewModel @ViewModelInject constructor(private val usersUseCases: UsersUseCases , private val mAuth: FirebaseAuth) : ViewModel() {
    private val _viewState = BehaviorSubject.create<UsersViewState>().apply {
        onNext(UsersViewState(
            users = listOf() , loading = true , loadingMore = false , error = null
            , refresh = false , creatingRoom = false , roomCreated = false , roomCreatedId = null , addingContact = false
            , createRoomError = null , addContactError = null, updateContactData = null,
            updateContactDataError = null , updatingContactData = false))
    }

    val cashedList = mutableListOf<UserWrapper>()

    val currentUser = mAuth.currentUser!!

    private val compositeDisposable = CompositeDisposable()

    fun viewState(): Observable<UsersViewState> = _viewState
    fun viewStateValue(): UsersViewState = _viewState.value!!

    private val _loadingUsers = PublishSubject.create<Unit>()
    private val _creatingRoom = PublishSubject.create<WatchRoom>()
    private val _addingContact = PublishSubject.create<String>()
    private val _updatingContact = PublishSubject.create<String>()

    init {
        bindUi()

        _loadingUsers.onNext(Unit)
    }

    private fun bindUi(){
        val disposable = Observable.merge(loadUsers() , createRoom() , addContact() , updateContact()).doOnNext {
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

    private fun updateContact(): Observable<UsersViewState>{
        return _updatingContact.switchMap { usersUseCases.getContactData(viewStateValue() , it) }
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
        postViewStateValue(viewStateValue().copy(creatingRoom = true , roomCreatedId = watchRoom.id))
        _creatingRoom.onNext(watchRoom)
    }

    fun addContact(query: String){
        if (viewStateValue().addingContact) return
        postViewStateValue(viewStateValue().copy(addingContact = true))
        _addingContact.onNext(query)
    }

    fun updateContact(userId: String){
        if (viewStateValue().updatingContactData) return
        postViewStateValue(viewStateValue().copy(updatingContactData = true))
        _updatingContact.onNext(userId)
    }

    fun clearStates(){
        postViewStateValue(viewStateValue().copy(roomCreated = false , createRoomError = null
            , creatingRoom = false , addContactError = null , addingContact = false , roomCreatedId = null
            ,updatingContactData = false , updateContactDataError = null , updateContactData = null))
    }


    private fun postViewStateValue(viewState: UsersViewState){
        _viewState.onNext(viewState)
    }

}