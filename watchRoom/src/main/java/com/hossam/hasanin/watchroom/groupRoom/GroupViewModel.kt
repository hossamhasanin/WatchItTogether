package com.hossam.hasanin.watchroom.groupRoom

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

class GroupViewModel @ViewModelInject constructor(private val useCase: GroupUseCase) : ViewModel() {
    private val _viewState = BehaviorSubject.create<GroupViewState>().apply {
        onNext(
            GroupViewState(listOf() , true , null , WatchRoom.READY)
        )
    }

    var currentUserState = UserState(id = User.current?.id!! , gender = User.current?.gender , name = User.current?.name!!
        , videoPosition = 0 , state = UserState.ENTERED , leader = false)

    var cashedRoomId: String? = null

    private val compositeDisposable = CompositeDisposable()

    fun viewState(): Observable<GroupViewState> = _viewState
    fun viewStateValue(): GroupViewState = _viewState.value!!

    private val _gettingUsers = PublishSubject.create<String>()
    private val _settingCurrentUserState = PublishSubject.create<Unit>()
    private val _leaving = PublishSubject.create<Unit>()

    init {
        bindUi()
    }

    private fun bindUi(){
        val dis = Observable.merge(_getUsers() , _leaveRoom()).doOnNext { postViewStateValue(it) }
            .observeOn(AndroidSchedulers.mainThread()).subscribe({} , {
                it.printStackTrace()
            })
        val dis2 = _setCurrentUserState().observeOn(AndroidSchedulers.mainThread()).subscribe({} , {
            it.printStackTrace()
        })
        compositeDisposable.addAll(dis , dis2)
    }

    private fun _getUsers(): Observable<GroupViewState> {
        return _gettingUsers.switchMap { useCase.usersListener(viewStateValue() , it) }
    }

    private fun _leaveRoom(): Observable<GroupViewState> {
        return _leaving.switchMap { useCase.leaveTheRoom(viewStateValue() , cashedRoomId!!) }
    }

    private fun _setCurrentUserState(): Observable<GroupViewState> {
        return _settingCurrentUserState.switchMap {
            Log.v("soso" , "_setCurrentUserState here $cashedRoomId")
            useCase.addCurrentUserState(viewStateValue() , cashedRoomId!! , currentUserState) }
    }

    fun setCurrentUserState() {
        val ids = viewStateValue().users.map { it.id }
        if (ids.contains(User.current?.id)) return
        _settingCurrentUserState.onNext(Unit)
    }

    fun leaveRoom(){
        _leaving.onNext(Unit)
    }

    fun getUsers(roomId: String , isLeader: Boolean){
        if (viewStateValue().users.isNotEmpty()) return
        cashedRoomId = roomId
        Log.v("soso" , "getUsers here $roomId")
        currentUserState = currentUserState.copy(leader = isLeader)
        _gettingUsers.onNext(roomId)
    }

    private fun postViewStateValue(viewState: GroupViewState){
        _viewState.onNext(viewState)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}