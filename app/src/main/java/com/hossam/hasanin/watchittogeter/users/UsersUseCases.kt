package com.hossam.hasanin.watchittogeter.users

import com.hossam.hasanin.base.models.WatchRoom
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
            it.error?.let {
                val v = viewState.copy(updateContactData = null , updateContactDataError = null , updatingContactData = false , createRoomError = it as Exception , creatingRoom = false , roomCreated = false)
                return@map v
            }
            return@map viewState.copy(updateContactData = null , updateContactDataError = null , updatingContactData = false , creatingRoom = false , roomCreated = true)
        }.toObservable().subscribeOn(Schedulers.io())
    }

//    fun updateUserWatchRoom(data: Map<String , Any>): Observable<UsersViewState>{
//        val room = (data["room"] as WatchRoom)
//        val viewState = (data["viewState"] as UsersViewState)
//        if (viewState.createRoomError != null){
//            return Observable.create<UsersViewState>{ viewState.copy(creatingRoom = false , roomCreated = false) }.subscribeOn(Schedulers.io())
//        } else {
//            return repo.updateUserWatchRoom(room.users[1], room.id).materialize<Unit>().map {
//                it.error?.let {
//                    return@map viewState.copy(createRoomError = it as Exception)
//                }
//                return@map viewState.copy(creatingRoom = false , roomCreated = true)
//            }.toObservable().subscribeOn(Schedulers.io())
//        }
//    }

    fun addContact(viewState: UsersViewState , query: String): Observable<UsersViewState>{
        return repo.addContacts(query).materialize().map {
            it.value?.let {
                val l = viewState.users.toMutableList()
                l.add(UserWrapper(it , UserWrapper.CONTENT))
                return@map viewState.copy(users = l , addingContact = false)
            }
            it.error?.let {
                return@map viewState.copy(addingContact = false , addContactError = it as Exception)
            }
            return@map viewState.copy(addContactError = null , addingContact = false)
        }.toObservable().subscribeOn(Schedulers.io())
    }

    fun getContactData(viewState: UsersViewState , userId: String): Observable<UsersViewState>{
        return repo.updateUserData(userId).materialize().map {
            it.value?.let {
                return@map viewState.copy(updateContactData = it, updateContactDataError = null , updatingContactData = false)
            }
            it.error?.let {
                return@map viewState.copy(updateContactData = null, updateContactDataError = it as Exception , updatingContactData = false)
            }
            return@map viewState.copy(updateContactData = null, updateContactDataError = null , updatingContactData = false)
        }.toObservable().subscribeOn(Schedulers.io())
    }

}