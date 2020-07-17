package com.hossam.hasanin.watchroom.groupRoom

import com.hossam.hasanin.watchittogeter.models.UserState
import com.hossam.hasanin.watchittogeter.repositories.MainRepository
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.lang.Exception
import javax.inject.Inject

class GroupUseCase @Inject constructor(private val repo: MainRepository) {
    fun usersListener(viewState: GroupViewState , roomId: String): Observable<GroupViewState>{
        return repo.roomUsersListener(roomId).materialize().map {
            it.value?.let {
                return@map viewState.copy(
                    users = it.map { UserStateWrapper(it.document.toObject(UserState::class.java) , UserStateWrapper.CONTENT) },
                    loading = false,
                    error = null
                )
            }
            it.error?.let {
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
        }.toObservable().subscribeOn(Schedulers.io())
    }
}