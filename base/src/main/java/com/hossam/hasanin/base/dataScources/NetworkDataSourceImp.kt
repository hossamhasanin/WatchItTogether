package com.hossam.hasanin.base.dataScources

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.hossam.hasanin.base.externals.ROOMS_COLLECTION
import com.hossam.hasanin.base.externals.ROOMS_PER_PAGE
import com.hossam.hasanin.base.externals.USERS_COLLECTION
import com.hossam.hasanin.base.externals.USERS_PER_PAGE
import com.hossam.hasanin.base.models.User
import com.hossam.hasanin.base.models.UserState
import com.hossam.hasanin.base.models.WatchRoom
import durdinapps.rxfirebase2.RxFirestore
import io.reactivex.Completable
import io.reactivex.CompletableEmitter
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
        val data = mapOf("id" to watchRoom.id ,
            "name" to watchRoom.name ,
            "desc" to watchRoom.desc ,
            "mp4Url" to watchRoom.mp4Url ,
            "state" to watchRoom.state , "users" to watchRoom.users , "createdAt" to FieldValue.serverTimestamp())
        return Completable.create{ emitter ->
            ref.set(data).addOnSuccessListener { emitter.onComplete() }.addOnFailureListener { emitter.onError(it) }
        }
    }

    override fun addOrUpdateCurrentUserState(roomId: String, userState: UserState , update: Boolean): Completable {
        return Completable.create {emmiter ->
            Log.v("soso" , "hello from com user id = ${User.current!!.id}")
            val ref  = roomsCollection.document(roomId).collection(USERS_COLLECTION).document(mAuth.currentUser!!.uid)
            if (update){
                ref.update("state" , UserState.READY)
                    .addOnSuccessListener {
                        Log.v("soso" , "hello from thr update")
                        emmiter.onComplete() }.addOnFailureListener {
                        it.printStackTrace()
                        emmiter.onError(it) }
            } else {
                ref.set(userState)
                    .addOnSuccessListener {
                        Log.v("soso" , "hello from set")
                        emmiter.onComplete() }.addOnFailureListener {
                        it.printStackTrace()
                        emmiter.onError(it) }
            }
        }
    }

    override fun getUserOut(roomId: String): Completable {
        return Completable.create {emitter ->
            Log.v("soso" , "getting out")
            removeUserState(roomId, emitter)
//            //users.remove(User.current!!.id)
//            updateRoomUsersList(roomId , users).addOnSuccessListener {
//                if (users.size > 0){
//                    removeUserState(roomId , emitter)
//                } else {
//                    roomsCollection.document(roomId).update("state" , WatchRoom.FINISHED)
//                        .addOnSuccessListener { emitter.onComplete() }.addOnFailureListener { emitter.onError(it) }
//                }
//            }.addOnFailureListener { emitter.onError(it) }
        }
    }

    override fun refreshUserState(roomId: String, userState: UserState): Completable {
        val ref = roomsCollection.document(roomId).collection(USERS_COLLECTION).document(mAuth.currentUser!!.uid)
        val data = mapOf("id" to userState.id
            , "name" to userState.name
            , "gender" to userState.gender
            , "state" to userState.state
            , "leader" to userState.leader
            , "videoPosition" to userState.videoPosition)
        return RxFirestore.updateDocument(ref , data)
    }

    private fun updateRoomUsersList(roomId: String , list: ArrayList<String>): Task<Void> {
        return roomsCollection.document(roomId).update("users" , list)
    }

    private fun removeUserState(roomId: String , emitter: CompletableEmitter){
        usersStateCollection(roomId).document(User.current!!.id!!).delete().addOnSuccessListener {
            Log.v("soso" , "get out successed")
            emitter.onComplete() }
            .addOnFailureListener {
                it.printStackTrace()
                emitter.onError(it) }
    }

    private fun usersStateCollection(roomId: String): CollectionReference {
        return roomsCollection.document(roomId).collection(USERS_COLLECTION)
    }

//    override fun updateUserWatchRoom(userId: String , roomId: String): Completable {
//        val user = userCollection.document(userId)
//        val me = userCollection.document(mAuth.currentUser!!.uid)
//        return Completable.create { emitter ->
//            user.update("currentRoomId" , roomId).addOnSuccessListener {
//                me.update("currentRoomId" , roomId).addOnSuccessListener { emitter.onComplete() }
//                    .addOnFailureListener { emitter.onError(Exception("Something go wrong")) }
//            }.addOnFailureListener { emitter.onError(Exception("Something go wrong")) }
//        }
//    }

    override fun updateCurrentRoomAndLastSeen(roomId: String): Completable {
        val data = mapOf<String , Any>("currentRoomId" to roomId , "lastSeen" to FieldValue.serverTimestamp())
        val ref = userCollection.document(mAuth.currentUser!!.uid)
        return RxFirestore.updateDocument(ref , data)
    }

    override fun updateCurrentRoomState(roomId: String , roomState: Int): Completable {
        return Completable.create {emmit ->
            roomsCollection.document(roomId).update("state" , roomState)
                .addOnSuccessListener { emmit.onComplete() }.addOnFailureListener { emmit.onError(it) }
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

    override fun updateUserData(userId: String): Maybe<User> {
        val ref = userCollection.document(userId)
        return RxFirestore.getDocument(ref , User::class.java)
    }

    override fun getWatchRoomsHistory(lastId: String): Maybe<List<WatchRoom>> {
        var query = roomsCollection.whereArrayContains("users" , mAuth.currentUser!!.uid)
            .orderBy("createdAt" , Query.Direction.DESCENDING).limit(ROOMS_PER_PAGE)
        query = if (lastId.isNotEmpty()) query.startAfter(lastId) else query
        return RxFirestore.getCollection(query , WatchRoom::class.java)
    }

    override fun getRoom(roomId: String): Maybe<WatchRoom> {
        val query = roomsCollection.whereEqualTo("id" , roomId).whereArrayContains("users" , mAuth.currentUser!!.uid)
        return Maybe.create{emitter ->
            query.get().addOnSuccessListener {
                if (!it.isEmpty){
                    val data = it.toObjects(WatchRoom::class.java)
                    emitter.onSuccess(data[0])
                }
            }.addOnFailureListener { emitter.onError(it) }
        }
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

    override fun roomStateListener(roomId: String): Observable<WatchRoom> {
        return Observable.create {emmiter ->
            roomsCollection.document(roomId)
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    if (firebaseFirestoreException != null){
                        emmiter.onError(firebaseFirestoreException)
                    } else {
                        val room = querySnapshot!!.toObject(WatchRoom::class.java)
                        Log.v("soso" , "room source ${room.toString()}")
                        if (room == null){
                            // send room removed exception so that it will get out of watchRoomActivity
                            emmiter.onError(Exception("The room has removed"))
                        } else {
                            emmiter.onNext(room)
                        }
                    }
                }
        }
    }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class NetworkDataSource