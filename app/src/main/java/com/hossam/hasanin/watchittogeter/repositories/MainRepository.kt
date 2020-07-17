package com.hossam.hasanin.watchittogeter.repositories

import com.google.firebase.firestore.DocumentChange
import com.hossam.hasanin.watchittogeter.models.User
import com.hossam.hasanin.watchittogeter.models.WatchRoom
import io.reactivex.Completable
import io.reactivex.Maybe

interface MainRepository {
    fun getUsersNetwork(lastId: String): Maybe<List<User>>
    fun createRoomNetwork(watchRoom: WatchRoom): Completable
    fun updateUserWatchRoom(userId: String , roomId: String): Completable
    fun addContacts(query: String): Maybe<User>

    fun getWatchRoomsHistory(lastId: String): Maybe<List<WatchRoom>>
    fun roomUsersListener(roomId: String): Maybe<List<DocumentChange>>
}