package com.hossam.hasanin.watchittogeter.dataScources

import com.hossam.hasanin.watchittogeter.models.User
import com.hossam.hasanin.watchittogeter.models.WatchRoom
import io.reactivex.Completable
import io.reactivex.Maybe

interface DataSource {

    fun getUsers(lastId: String): Maybe<List<User>>
    fun createRoom(watchRoom: WatchRoom): Completable
    fun updateUserWatchRoom(userId: String , roomId: String): Completable
    fun addContact(query: String): Maybe<User>

    fun getWatchRoomsHistory(lastId: String): Maybe<List<WatchRoom>>
}