package com.hossam.hasanin.watchittogeter.repositories

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.hossam.hasanin.watchittogeter.dataScources.DataSource
import com.hossam.hasanin.watchittogeter.dataScources.NetworkDataSourceImp
import com.hossam.hasanin.watchittogeter.models.User
import com.hossam.hasanin.watchittogeter.models.WatchRoom
import io.reactivex.Completable
import io.reactivex.Maybe
import javax.inject.Inject

class MainRepositoryImp @Inject constructor(private val networkDataSourceImp: DataSource): MainRepository {
    override fun getUsersNetwork(lastId: String): Maybe<List<User>> {
        return networkDataSourceImp.getUsers(lastId)
    }

    override fun createRoomNetwork(watchRoom: WatchRoom): Completable {
        return networkDataSourceImp.createRoom(watchRoom)
    }

    override fun updateUserWatchRoom(userId: String, roomId: String): Completable {
        return networkDataSourceImp.updateUserWatchRoom(userId , roomId)
    }

    override fun addContacts(query: String): Maybe<User> {
        return networkDataSourceImp.addContact(query)
    }

    override fun getWatchRoomsHistory(lastId: String): Maybe<List<WatchRoom>> {
        return networkDataSourceImp.getWatchRoomsHistory(lastId)
    }

    override fun roomUsersListener(roomId: String): Maybe<List<DocumentChange>> {
        return networkDataSourceImp.roomUsersListener(roomId)
    }
}