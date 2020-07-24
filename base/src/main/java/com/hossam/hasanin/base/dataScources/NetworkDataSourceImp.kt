package com.hossam.hasanin.base.dataScources

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.hossam.hasanin.base.externals.ROOMS_COLLECTION
import com.hossam.hasanin.base.externals.ROOMS_PER_PAGE
import com.hossam.hasanin.base.externals.USERS_COLLECTION
import com.hossam.hasanin.base.externals.USERS_PER_PAGE
import com.hossam.hasanin.base.models.User
import com.hossam.hasanin.base.models.UserState
import com.hossam.hasanin.base.models.WatchRoom
import durdinapps.rxfirebase2.RxFirestore
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Qualifier

class NetworkDataSourceImp @Inject constructor(private val firestore: FirebaseFirestore, private val mAuth: FirebaseAuth):
    DataSource {
    private val userCollection = firestore.collection(USERS_COLLECTION)
    private val roomsCollection = firestore.collection(ROOMS_COLLECTION)

    override fun getUsers(lastId: String): Maybe<List<User>> {
        var query = userCollection.orderBy("id" , Query.Direction.ASCENDING).whereArrayContains("addedBy" , mAuth.currentUser!!.uid).limit(
            USERS_PER_PAGE
        )
        query = if (lastId.isNotEmpty()) query.startAfter(lastId) else query
        return RxFirestore.getCollection(query , User::class.java)
    }

    override fun createRoom(watchRoom: WatchRoom): Completable {
        val ref = roomsCollection.document(watchRoom.id)
        return RxFirestore.setDocument(ref , watchRoom)
    }

    override fun addCurrentUserState(roomId: String , userState: UserState): Completable {
        return Completable.create {emmiter ->
            roomsCollection.document(roomId).collection(USERS_COLLECTION).document(User.current?.id!!).set(userState)
                .addOnSuccessListener { emmiter.onComplete() }.addOnFailureListener { emmiter.onError(it) }
        }
    }

    override fun updateUserWatchRoom(userId: String , roomId: String): Completable {
        val user = userCollection.document(userId)
        val me = userCollection.document(mAuth.currentUser!!.uid)
        return Completable.create { emitter ->
            user.update("currentRoomId" , roomId).addOnSuccessListener {
                me.update("currentRoomId" , roomId).addOnSuccessListener { emitter.onComplete() }
                    .addOnFailureListener { emitter.onError(Exception("Something go wrong")) }
            }.addOnFailureListener { emitter.onError(Exception("Something go wrong")) }
        }
    }

    override fun addContact(query: String): Maybe<User> {
        val ref = if (query.contains("@")) userCollection.whereEqualTo("email" , query)
        else userCollection.whereEqualTo("phone" , query)
        return Maybe.create { emitter ->
            ref.get().addOnSuccessListener {
                val users = it.toObjects(User::class.java)
                val ids = users.map { it.id }
                if (users.isNotEmpty()){
                    if (!ids.contains(User.current!!.id)) {
                        val user = users[0]
                        val update = userCollection.document(user.id!!)
                        val addedBy = user.addedBy
                        addedBy.add(mAuth.currentUser!!.uid)
                        update.update("addedBy", addedBy)
                            .addOnSuccessListener { emitter.onSuccess(user) }
                            .addOnFailureListener { emitter.onError(Exception("Error while add the contact")) }
                    } else {
                        emitter.onError(Exception("Buddy you can not add yourself !"))
                    }
                } else {
                    emitter.onError(Exception("No user Found"))
                }
            }.addOnFailureListener { emitter.onError(it) }
        }
    }

    override fun getWatchRoomsHistory(lastId: String): Maybe<List<WatchRoom>> {
        var query = roomsCollection.whereArrayContains("users" , mAuth.currentUser!!.uid)
            .orderBy("timestamp" , Query.Direction.DESCENDING).limit(ROOMS_PER_PAGE)
        query = if (lastId.isNotEmpty()) query.startAfter(lastId) else query
        return RxFirestore.getCollection(query , WatchRoom::class.java)
    }

    override fun roomUsersListener(roomId: String): Observable<List<DocumentSnapshot>> {
        return Observable.create {emmiter ->
            roomsCollection.document(roomId).collection(USERS_COLLECTION)
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    val users = querySnapshot?.documents
                    if (firebaseFirestoreException != null){
                        emmiter.onError(firebaseFirestoreException)
                    } else {
                        Log.v("soso" , "source ${users.toString()}")
                        emmiter.onNext(users!!)
                    }
            }
        }
    }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class NetworkDataSource