package com.hossam.hasanin.watchittogeter.watchRooms

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.hossam.hasanin.base.models.User
import com.hossam.hasanin.base.models.UserState
import com.hossam.hasanin.base.models.WatchRoom
import com.hossam.hasanin.watchittogeter.users.UserWrapper
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import java.lang.Exception

class WatchRoomsViewModel @ViewModelInject constructor(private val useCase: WatchRoomsUseCase) : ViewModel() {
    private val _viewState = BehaviorSubject.create<WatchRoomsViewState>().apply {
        onNext(
            WatchRoomsViewState(
                rooms = listOf() , loading = true , loadingMore = false , error = null
            , refresh = false , searchingRoom = false , searchedRoom = null , searchError = null,
            enteringTheRoom = false , enteredRoom = null , errorEntering = null)
        )
    }

    val cashedList = mutableListOf<RoomWrapper>()

    private val compositeDisposable = CompositeDisposable()

    fun viewState(): Observable<WatchRoomsViewState> = _viewState
    fun viewStateValue(): WatchRoomsViewState = _viewState.value!!

    private val _loadingRooms = PublishSubject.create<Unit>()
    private val _gettingRoom = PublishSubject.create<String>()
    private val _enteringRoom = PublishSubject.create<Map<String , Any>>()

    init {
        bindUi()

        _loadingRooms.onNext(Unit)
    }

    private fun bindUi(){
        val disposable = Observable.merge(loadRooms() , getRoom() , enterRoom()).doOnNext {
            postViewStateValue(it)
        }.observeOn(AndroidSchedulers.mainThread()).subscribe({}, {
            it.printStackTrace()
        })

        compositeDisposable.add(disposable)
    }

    fun loadMore(){
        if (viewStateValue().loadingMore || viewStateValue().refresh) return
        val list = viewStateValue().rooms.toMutableList()
        list.add(RoomWrapper(null , UserWrapper.LOADING))
        postViewStateValue(
            viewStateValue().copy(rooms = list , loadingMore = true ,
                searchedRoom = null , searchError = null , searchingRoom = false)
        )
        _loadingRooms.onNext(Unit)
    }

    fun refresh(){
        if (viewStateValue().loadingMore || viewStateValue().refresh) return
        postViewStateValue(viewStateValue().copy(refresh = true ,
            searchedRoom = null , searchError = null , searchingRoom = false))
    }

    private fun loadRooms(): Observable<WatchRoomsViewState>{
        return _loadingRooms.switchMap { useCase.getWatchRoomsHistory(viewStateValue()) }
    }

    private fun getRoom(): Observable<WatchRoomsViewState>{
        return _gettingRoom.switchMap { useCase.getRoom(viewStateValue() , it) }
    }

    private fun enterRoom(): Observable<WatchRoomsViewState>{
        return _enteringRoom.switchMap { useCase.addUserStateToTheRoom(viewStateValue() ,
            it["roomId"] as String , it["userState"] as UserState , it["room"] as WatchRoom) }
    }

    fun searchFor(roomId:String){
        if (viewStateValue().searchingRoom) return
        postViewStateValue(viewStateValue().copy(searchingRoom = true ,
            searchedRoom = null , searchError = null))
        _gettingRoom.onNext(roomId)
    }

    fun enteringRoom(roomId: String , room: WatchRoom){
        if (viewStateValue().enteringTheRoom) return
        val userCurrentState = when(room.state){
            WatchRoom.PREPARING->{ UserState.ENTERED }
            WatchRoom.RUNNING->{UserState.PLAYING}
            else->{throw  Exception("No such state")}
        }
        val userState = UserState(name = User.current!!.name , id = User.current!!.id!! , state = userCurrentState ,
        leader = false , videoPosition = 0)
        val data = mapOf<String , Any>("roomId" to roomId , "userState" to userState , "room" to room)
        postViewStateValue(viewStateValue().copy(searchedRoom = null , searchingRoom = false , enteringTheRoom = true , errorEntering = null , enteredRoom = null))
        _enteringRoom.onNext(data)
    }

    private fun postViewStateValue(viewState: WatchRoomsViewState){
        _viewState.onNext(viewState)
    }

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
}