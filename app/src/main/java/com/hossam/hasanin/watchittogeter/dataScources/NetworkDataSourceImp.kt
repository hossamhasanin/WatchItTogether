package com.hossam.hasanin.watchittogeter.dataScources

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.hossam.hasanin.watchittogeter.externals.ROOMS_COLLECTION
import com.hossam.hasanin.watchittogeter.externals.USERS_COLLECTION
import com.hossam.hasanin.watchittogeter.externals.USERS_PER_PAGE
import com.hossam.hasanin.watchittogeter.models.User
import com.hossam.hasanin.watchittogeter.models.WatchRoom
import durdinapps.rxfirebase2.RxFirestore
import io.reactivex.Completable
import io.reactivex.Maybe
import javax.inject.Inject
import javax.inject.Qualifier

class NetworkDataSourceImp @Inject constructor(private val firestore: FirebaseFirestore, private val mAuth: FirebaseAuth): DataSource {
    private val userCollection = firestore.collection(USERS_COLLECTION)
    private val roomsCollection = firestore.collection(ROOMS_COLLECTION)

    override fun getUsers(lastId: String): Maybe<List<User>> {
        var query = userCollection.orderBy("id" , Query.Direction.ASCENDING).limit(
            USERS_PER_PAGE)
        query = if (lastId.isNotEmpty()) userCollection.startAfter(lastId) else userCollection
        return RxFirestore.getCollection(userCollection , User::class.java)
    }

    override fun createRoom(watchRoom: WatchRoom): Completable {
        val ref = roomsCollection.document(watchRoom.id)
        return RxFirestore.setDocument(ref , watchRoom)
    }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class NetworkDataSource