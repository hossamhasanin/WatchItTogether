package com.hossam.hasanin.watchroom.groupRoom

import android.util.Log
import android.view.View
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

class GroupViewModel @ViewModelInject constructor(private val useCase: GroupUseCase) : ViewModel() {
    private val _viewState = BehaviorSubject.create<GroupViewState>().apply {
        onNext(
            GroupViewState(users = listOf() , loading = true , error = null ,
                roomSate = WatchRoom.PREPARING , roomStateError = null ,
                updatingRoomState = false)
        )
    }

//     val usersState = PublishSubject.create<List<UserState>>()

    var currentUserState = UserState(id = User.current?.id!! , gender = User.current?.gender , name = User.current?.name!!
        , videoPosition = 0 , state = UserState.ENTERED , leader = false)

    var cashedRoomId: String? = null

    private val compositeDisposable = CompositeDisposable()

    fun viewState(): Observable<GroupViewState> = _viewState
    fun viewStateValue(): GroupViewState = _viewState.value!!

    private val _gettingUsers = PublishSubject.create<String>()
    private val _roomStateListening = PublishSubject.create<String>()
    private val _updatingLastSeenData = PublishSubject.create<String>()
    private val _updatingRoomState = PublishSubject.create<Int>()
    private val _settingCurrentUserState = PublishSubject.create<Boolean>()


    init {
        bindUi()
    }

    private fun bindUi(){
        val dis = Observable.merge(_updateLastSeenData() , _setCurrentUserState())
            .observeOn(AndroidSchedulers.mainThread()).subscribe({} , {
                it.printStackTrace()
            })
        val dis2 = Observable.merge(_roomStateListener() , _getUsers() , _updateRoomState()).doOnNext { postViewStateValue(it) }
            .observeOn(AndroidSchedulers.mainThread()).subscribe({} , {
                it.printStackTrace()
            })
//        val dis3 = .doOnNext { usersState.onNext(it) }.observeOn(AndroidSchedulers.mainThread()).subscribe({} , {
//            it.printStackTrace()
//        })
        compositeDisposable.addAll(dis , dis2)
    }

    private fun _getUsers(): Observable<GroupViewState> {
        return _gettingUsers.switchMap { useCase.usersListener(viewStateValue() , it) }
    }

    private fun _updateLastSeenData(): Observable<Unit> {
        return _updatingLastSeenData.switchMap { useCase.updateLastSeenData(it) }
    }

    fun _roomStateListener(): Observable<GroupViewState>{
        return _roomStateListening.switchMap { useCase.roomStateListener(viewStateValue() , cashedRoomId!!) }
    }

    private fun _updateRoomState(): Observable<GroupViewState> {
        return _updatingRoomState.switchMap { useCase.updateRoomState(viewStateValue() , cashedRoomId!! , it) }
    }


    private fun _setCurrentUserState(): Observable<Unit> {
        return _settingCurrentUserState.switchMap {
            Log.v("soso" , "_setCurrentUserState here $cashedRoomId")
            useCase.addOrUpdateCurrentUserState(cashedRoomId!! , currentUserState , it) }
    }

    fun initCurrentUserState() {
        val ids = viewStateValue().users.map { it.id }
        if (ids.contains(User.current?.id)) return
        addOrUpdateUserState(false)
    }

    fun addOrUpdateUserState(update: Boolean){
        _settingCurrentUserState.onNext(update)
    }

    fun updateRoomState(roomState: Int){
        if (viewStateValue().updatingRoomState) return
        _updatingRoomState.onNext(roomState)
    }

//    fun leaveRoom(){
//        _leaving.onNext(Unit)
//    }

    fun getUsers(roomId: String , isLeader: Boolean){
        if (viewStateValue().users.isNotEmpty()) return
        cashedRoomId = roomId
        Log.v("soso" , "getUsers here $roomId")
        currentUserState = currentUserState.copy(leader = isLeader)
        _gettingUsers.onNext(roomId)
        _roomStateListening.onNext(roomId)
        _updatingLastSeenData.onNext(roomId)
    }

    private fun postViewStateValue(viewState: GroupViewState){
        _viewState.onNext(viewState)
    }

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
}