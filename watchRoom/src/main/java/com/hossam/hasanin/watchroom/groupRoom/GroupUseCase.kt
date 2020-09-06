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

    fun roomStateListener(viewState: GroupViewState , roomId: String): Observable<GroupViewState>{
        return repo.roomStateListener(roomId).materialize().map {
            it.value?.let {
                return@map viewState.copy(
                    roomSate = it.state!!,
                    loading = false,
                    roomStateError = null
                )
            }
            it.error?.let {
                //Log.v("soso" , it.toString())
                return@map viewState.copy(
                    roomSate = null,
                    loading = false,
                    roomStateError = it as Exception
                )
            }
            return@map viewState.copy(
                roomSate = null,
                loading = false,
                roomStateError = null
            )
        }.subscribeOn(Schedulers.io())
    }

    fun addOrUpdateCurrentUserState(roomId: String, userState: UserState, update: Boolean): Observable<Unit>{
        return repo.addOrUpdateCurrentUserState(roomId, userState , update).materialize<Unit>().map {
            Unit
        }.toObservable().subscribeOn(Schedulers.io())
    }

//    fun leaveTheRoom(viewState: GroupViewState , roomId: String): Observable<GroupViewState>{
//        val ids = viewState.users.map { it.id } as ArrayList
//        return repo.getUserOut(roomId , ids).materialize<Unit>().map {
//            it.value?.let {
//                return@map viewState
//            }
//            it.error?.let {
//                return@map viewState.copy(error = it as Exception)
//            }
//            return@map viewState
//        }.toObservable().subscribeOn(Schedulers.io())
//    }

    fun updateLastSeenData(roomId: String) : Observable<Unit>{
        return repo.updateCurrentRoomAndLastSeen(roomId).materialize<Unit>().map {
            Unit
        }.toObservable().subscribeOn(Schedulers.io())
    }

    fun updateRoomState(viewState: GroupViewState, roomId: String , roomState: Int) : Observable<GroupViewState>{
        return repo.updateCurrentRoomState(roomId , roomState).materialize<Unit>().map {
            return@map viewState.copy(updatingRoomState = false)
        }.toObservable().subscribeOn(Schedulers.io())
    }

}