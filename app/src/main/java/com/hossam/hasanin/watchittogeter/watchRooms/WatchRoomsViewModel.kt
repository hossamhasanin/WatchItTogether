package com.hossam.hasanin.watchittogeter.watchRooms

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.hossam.hasanin.watchittogeter.users.UserWrapper
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

class WatchRoomsViewModel @ViewModelInject constructor(private val useCase: WatchRoomsUseCase) : ViewModel() {
    private val _viewState = BehaviorSubject.create<WatchRoomsViewState>().apply {
        onNext(
            WatchRoomsViewState(rooms = listOf() , loading = true , loadingMore = false , error = null
            , refresh = false )
        )
    }

    val cashedList = mutableListOf<RoomWrapper>()

    private val compositeDisposable = CompositeDisposable()

    fun viewState(): Observable<WatchRoomsViewState> = _viewState
    fun viewStateValue(): WatchRoomsViewState = _viewState.value!!

    private val _loadingRooms = PublishSubject.create<Unit>()

    init {
        bindUi()

        _loadingRooms.onNext(Unit)
    }

    private fun bindUi(){
        val disposable = loadRooms().doOnNext {
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
            viewStateValue().copy(rooms = list , loadingMore = true)
        )
        _loadingRooms.onNext(Unit)
    }

    private fun loadRooms(): Observable<WatchRoomsViewState>{
        return _loadingRooms.switchMap { useCase.getWatchRoomsHistory(viewStateValue()) }
    }

    private fun postViewStateValue(viewState: WatchRoomsViewState){
        _viewState.onNext(viewState)
    }
}