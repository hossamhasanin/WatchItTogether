package com.hossam.hasanin.watchittogeter.watchRooms

import com.hossam.hasanin.watchittogeter.repositories.MainRepository
import com.hossam.hasanin.watchittogeter.users.UserWrapper
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.lang.Exception
import javax.inject.Inject

class WatchRoomsUseCase @Inject constructor(private val repo: MainRepository){

    fun getWatchRoomsHistory(viewState: WatchRoomsViewState): Observable<WatchRoomsViewState> {
        val lastId = if (viewState.loadingMore && !viewState.refresh)
            viewState.rooms[viewState.rooms.lastIndex].room?.id!!
        else ""

        return repo.getWatchRoomsHistory(lastId).materialize().map {
            it.value?.let {
                return@map viewState.copy(
                    rooms = it.map { RoomWrapper(it , UserWrapper.CONTENT) },
                    error = null,
                    loading = false,
                    loadingMore = false
                )
            }
            it.error?.let {
                return@map viewState.copy(
                    rooms = listOf(),
                    error = it as Exception,
                    loadingMore = false,
                    loading = false
                )
            }
            return@map viewState.copy(
                rooms = listOf(),
                error = null,
                loadingMore = false,
                loading = false
            )
        }.toObservable().subscribeOn(Schedulers.io())
    }

}