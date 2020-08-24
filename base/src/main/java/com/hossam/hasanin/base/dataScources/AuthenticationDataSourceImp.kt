package com.hossam.hasanin.base.dataScources

import android.util.Log
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.hossam.hasanin.base.externals.USERS_COLLECTION
import com.hossam.hasanin.base.models.User
import io.reactivex.Maybe
import javax.inject.Inject

class AuthenticationDataSourceImp @Inject constructor(private val mAuth: FirebaseAuth , private val firestore: FirebaseFirestore) :
    AuthenticationDataSource {
    override fun login(email: String, pass: String): Maybe<AuthResult> {
        return Maybe.create {emmiter ->
            mAuth.signInWithEmailAndPassword(email , pass).addOnSuccessListener {auth->
                firestore.collection(USERS_COLLECTION).document(auth.user!!.uid).get()
                    .addOnSuccessListener {
                        User.current = it.toObject(User::class.java)
                        Log.v("koko" , User.current.toString())
                        emmiter.onSuccess(auth)
                    }
                    .addOnFailureListener {
                        it.printStackTrace()
                        emmiter.onError(it) }
            }.addOnFailureListener { emmiter.onError(it) }
        }
    }

    override fun signup(user: User, pass: String): Maybe<AuthResult> {
       return Maybe.create { emmit ->
            mAuth.createUserWithEmailAndPassword(user.email!! , pass).addOnSuccessListener { auth->
                val u = user.copy(id = auth.user!!.uid)
                User.current = u
                val data = mapOf("id" to u.id ,
                    "name" to u.name ,
                    "email" to u.email ,
                    "currentRoomId" to u.currentRoomId ,
                    "addedBy" to u.addedBy ,
                    "gender" to u.gender ,
                    "phone" to u.phone ,
                    "lastSeen" to u.lastSeen ,
                    "createdAt" to FieldValue.serverTimestamp())
                firestore.collection(USERS_COLLECTION).document(auth.user!!.uid).set(data)
                    .addOnSuccessListener { emmit.onSuccess(auth) }
                    .addOnFailureListener { emmit.onError(Throwable("Error in saving user data")) }
            }.addOnFailureListener { emmit.onError(Throwable(it.localizedMessage)) }
        }
    }
}