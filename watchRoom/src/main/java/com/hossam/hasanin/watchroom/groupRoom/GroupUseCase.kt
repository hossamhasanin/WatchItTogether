package com.hossam.hasanin.watchroom.groupRoom

import android.util.Log
import com.hossam.hasanin.base.models.UserState
import com.hossam.hasanin.watchittogeter.repositories.MainRepository
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.lang.Exception
import javax.inject.Inject

class GroupUseCase @Inject constructor(private val repo: MainRepository) {
    fun usersListener(viewState: GroupViewState , roomId: String): Observable<GroupViewState>{
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

    fun addCurrentUserState(viewState: GroupViewState , roomId: String , userState: UserState): Observable<GroupViewState>{
        return repo.addCurrentUserState(roomId, userState).materialize<Unit>().map {
            return@map viewState
        }.toObservable().subscribeOn(Schedulers.io())
    }

}