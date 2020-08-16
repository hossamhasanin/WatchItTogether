package com.hossam.hasanin.base.dataScources

import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentSnapshot
import com.hossam.hasanin.base.models.User
import com.hossam.hasanin.base.models.UserState
import com.hossam.hasanin.base.models.WatchRoom
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable

interface DataSource {

    fun getUsers(lastId: String): Maybe<List<User>>
    fun addContact(query: String): Maybe<User>
    fun updateUserData(userId: String): Maybe<User>

    fun getWatchRoomsHistory(lastId: String): Maybe<List<WatchRoom>>
    fun updateUserWatchRoom(userId: String , roomId: String): Completable
    fun roomUsersListener(roomId: String): Observable<List<DocumentSnapshot>>
    fun createRoom(watchRoom: WatchRoom): Completable
    fun addCurrentUserState(roomId: String , userState: UserState): Completable
    fun getUserOut(roomId: String , users: ArrayList<String>): Completable

    fun setUserState(roomId: String , userState: UserState): Completable
}