package com.hossam.hasanin.watchittogeter.users

import com.hossam.hasanin.watchittogeter.models.WatchRoom
import com.hossam.hasanin.watchittogeter.repositories.MainRepository
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.lang.Exception
import javax.inject.Inject

class UsersUseCases @Inject constructor(private val repo: MainRepository) {
    fun getUsers(viewState: UsersViewState): Observable<UsersViewState>{
        val lastId = if (viewState.loadingMore && !viewState.refresh)
            viewState.users[viewState.users.lastIndex].user?.id!!
         else ""

        return repo.getUsersNetwork(lastId).materialize().map {
            it.value?.let {
                return@map viewState.copy(
                    users = it.map { UserWrapper(it , UserWrapper.CONTENT) },
                    error = null,
                    loading = false,
                    loadingMore = false,
                    roomCreated = false,
                    creatingRoom = false
                )
            }
            it.error?.let {
                return@map viewState.copy(
                    users = listOf(),
                    error = it as Exception,
                    loadingMore = false,
                    loading = false,
                    roomCreated = false,
                    creatingRoom = false
                )
            }
            return@map viewState.copy(
                users = listOf(),
                error = null,
                loadingMore = false,
                loading = false,
                roomCreated = false,
                creatingRoom = false
            )
        }.toObservable().subscribeOn(Schedulers.io())
    }

    fun createRoom(viewState: UsersViewState , watchRoom: WatchRoom): Observable<UsersViewState>{
        return repo.createRoomNetwork(watchRoom).materialize<Unit>().map {
            return@map viewState.copy(creatingRoom = false , roomCreated = true)
        }.toObservable().subscribeOn(Schedulers.io())
    }

}