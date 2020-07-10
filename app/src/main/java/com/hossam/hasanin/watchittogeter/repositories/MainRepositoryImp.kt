package com.hossam.hasanin.watchittogeter.repositories

import com.google.firebase.auth.FirebaseAuth
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
}