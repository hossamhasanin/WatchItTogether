package com.hossam.hasanin.watchittogeter.repositories

import com.hossam.hasanin.watchittogeter.models.User
import com.hossam.hasanin.watchittogeter.models.WatchRoom
import io.reactivex.Completable
import io.reactivex.Maybe

interface MainRepository {
    fun getUsersNetwork(lastId: String): Maybe<List<User>>
    fun createRoomNetwork(watchRoom: WatchRoom): Completable
}