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
    fun getRoom(roomId: String): Maybe<WatchRoom>
//    fun updateUserWatchRoom(userId: String , roomId: String): Completable
    fun updateCurrentRoomAndLastSeen(roomId: String): Completable
    fun updateCurrentRoomState(roomId: String , roomState: Int): Completable
    fun roomUsersListener(roomId: String): Observable<List<DocumentSnapshot>>
    fun roomStateListener(roomId: String): Observable<WatchRoom>
    fun createRoom(watchRoom: WatchRoom): Completable
    fun addOrUpdateCurrentUserState(roomId: String, userState: UserState , update: Boolean): Completable
    fun getUserOut(roomId: String): Completable

    fun refreshUserState(roomId: String, userState: UserState): Completable
}