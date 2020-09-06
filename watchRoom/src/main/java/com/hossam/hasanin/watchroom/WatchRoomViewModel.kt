package com.hossam.hasanin.watchroom

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.hossam.hasanin.watchittogeter.repositories.MainRepository
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

class WatchRoomViewModel @ViewModelInject constructor(private val repo: MainRepository) : ViewModel() {


    private val _leavingRoom = PublishSubject.create<String>()
    private val compositeDisposable = CompositeDisposable()

    val leftSuccessed = PublishSubject.create<Unit>()

    init {
        val dis = _leaveRoom().subscribeOn(Schedulers.io()).subscribe({
            leftSuccessed.onNext(Unit)
        } , {
            it.printStackTrace()
        })
        compositeDisposable.add(dis)
    }

    private fun _leaveRoom(): Observable<Unit>{
        return _leavingRoom.switchMap { leaveTheRoomUseCase(it) }
    }

    private fun leaveTheRoomUseCase(roomId: String): Observable<Unit> {
        return repo.getUserOut(roomId).materialize<Unit>().map { Unit }.toObservable().subscribeOn(Schedulers.io())
    }

    fun leave(roomId: String){
        _leavingRoom.onNext(roomId)
    }

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
}