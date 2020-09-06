package com.hossam.hasanin.watchroom.playRoom

import android.util.Log
import com.hossam.hasanin.base.models.UserState
import com.hossam.hasanin.watchittogeter.repositories.MainRepository
import com.hossam.hasanin.watchroom.groupRoom.GroupViewState
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.lang.Exception
import javax.inject.Inject

class PlayUseCase @Inject constructor(private val repo: MainRepository) {

    fun usersStateListener(viewState: PlayViewState , roomId: String) : Observable<PlayViewState> {
        return repo.roomUsersListener(roomId).materialize().map {
            it.value?.let {
                val l = it.map { it.toObject(UserState::class.java)!! }
                Log.v("soso" , "use case $l")
                return@map viewState.copy(
                    users = l,
                    loading = false,
                    error = null
                )
            }
            it.error?.let {
                //Log.v("soso" , it.toString())
                return@map viewState.copy(
                    users = listOf(),
                    loading = false,
                    error = it as Exception
                )
            }
            return@map viewState.copy(
                users = listOf(),
                loading = false,
                error = null
            )
        }.subscribeOn(Schedulers.io())
    }

    fun refreshUserState(userState: UserState, roomId: String): Observable<UserState>{
        return repo.refreshUserState(roomId, userState).materialize<Unit>().map {
            userState
        }.toObservable().subscribeOn(Schedulers.io())
    }

}