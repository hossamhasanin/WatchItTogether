package com.hossam.hasanin.base.repositories

import com.google.firebase.firestore.DocumentSnapshot
import com.hossam.hasanin.base.dataScources.DataSource
import com.hossam.hasanin.base.models.User
import com.hossam.hasanin.base.models.UserState
import com.hossam.hasanin.base.models.WatchRoom
import com.hossam.hasanin.watchittogeter.repositories.MainRepository
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import javax.inject.Inject

class MainRepositoryImp @Inject constructor(private val networkDataSourceImp: DataSource):
    MainRepository {
    override fun getUsersNetwork(lastId: String): Maybe<List<User>> {
        return networkDataSourceImp.getUsers(lastId)
    }

    override fun createRoomNetwork(watchRoom: WatchRoom): Completable {
        return networkDataSourceImp.createRoom(watchRoom)
    }

//    override fun updateUserWatchRoom(userId: String, roomId: String): Completable {
//        return networkDataSourceImp.updateUserWatchRoom(userId , roomId)
//    }

    override fun updateCurrentRoomAndLastSeen(roomId: String): Completable {
        return networkDataSourceImp.updateCurrentRoomAndLastSeen(roomId)
    }

    override fun addContacts(query: String): Maybe<User> {
        return networkDataSourceImp.addContact(query)
    }

    override fun updateUserData(userId: String): Maybe<User> {
        return networkDataSourceImp.updateUserData(userId)
    }

    override fun getWatchRoomsHistory(lastId: String): Maybe<List<WatchRoom>> {
        return networkDataSourceImp.getWatchRoomsHistory(lastId)
    }

    override fun getRoom(roomId: String): Maybe<WatchRoom> {
        return networkDataSourceImp.getRoom(roomId)
    }

    override fun roomUsersListener(roomId: String): Observable<List<DocumentSnapshot>> {
        return networkDataSourceImp.roomUsersListener(roomId)
    }

    override fun roomStateListener(roomId: String): Observable<WatchRoom> {
        return networkDataSourceImp.roomStateListener(roomId)
    }

    override fun getUserOut(roomId: String): Completable {
        return networkDataSourceImp.getUserOut(roomId)
    }

    override fun addOrUpdateCurrentUserState(roomId: String, userState: UserState , update: Boolean): Completable {
        return networkDataSourceImp.addOrUpdateCurrentUserState(roomId , userState , update)
    }

    override fun updateCurrentRoomState(roomId: String, roomState: Int): Completable {
        return networkDataSourceImp.updateCurrentRoomState(roomId, roomState)
    }

    override fun refreshUserState(roomId: String, userState: UserState): Completable {
        return networkDataSourceImp.refreshUserState(roomId , userState)
    }
}