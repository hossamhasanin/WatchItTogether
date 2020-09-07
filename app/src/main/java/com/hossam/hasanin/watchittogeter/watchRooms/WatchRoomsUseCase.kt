package com.hossam.hasanin.watchittogeter.watchRooms

import com.hossam.hasanin.base.models.UserState
import com.hossam.hasanin.base.models.WatchRoom
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
                    loadingMore = false,
                    refresh = false,
                    searchedRoom = null , searchError = null , searchingRoom = false
                )
            }
            it.error?.let {
                return@map viewState.copy(
                    rooms = listOf(),
                    error = it as Exception,
                    loadingMore = false,
                    loading = false,
                    refresh = false,
                    searchedRoom = null , searchError = null , searchingRoom = false
                )
            }
            return@map viewState.copy(
                rooms = listOf(),
                error = null,
                loadingMore = false,
                loading = false,
                refresh = false,
                searchedRoom = null , searchError = null , searchingRoom = false
            )
        }.toObservable().subscribeOn(Schedulers.io())
    }

    fun getRoom(viewState: WatchRoomsViewState , roomId: String): Observable<WatchRoomsViewState>{
        return repo.getRoom(roomId).materialize().map {
            it.value?.let {
                return@map viewState.copy(
                    searchedRoom = it,
                    searchError = null,
                    searchingRoom = false
                )
            }
            it.error?.let {
                return@map viewState.copy(
                    searchError = it as Exception,
                    searchingRoom = false,
                    searchedRoom = null
                )
            }
            return@map viewState.copy(
                searchingRoom = false,
                searchedRoom = null,
                searchError = null
            )
        }.toObservable().subscribeOn(Schedulers.io())
    }

    fun addUserStateToTheRoom(viewState: WatchRoomsViewState, roomId: String, userState:UserState, room: WatchRoom): Observable<WatchRoomsViewState>{
        return repo.addOrUpdateCurrentUserState(roomId , userState , false).materialize<Unit>().map {
            it.error?.let {
                return@map viewState.copy(
                    enteringTheRoom = false,
                    errorEntering = it as Exception,
                    enteredRoom = null
                )
            }
            return@map viewState.copy(
                enteringTheRoom = false,
                errorEntering = null,
                enteredRoom = room
            )
        }.toObservable().subscribeOn(Schedulers.io())
    }

}