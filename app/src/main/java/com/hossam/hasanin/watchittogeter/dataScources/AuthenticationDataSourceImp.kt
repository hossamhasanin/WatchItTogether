package com.hossam.hasanin.watchittogeter.dataScources

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.hossam.hasanin.watchittogeter.externals.USERS_COLLECTION
import com.hossam.hasanin.watchittogeter.models.User
import durdinapps.rxfirebase2.RxFirebaseAuth
import io.reactivex.Maybe
import javax.inject.Inject

class AuthenticationDataSourceImp @Inject constructor(private val mAuth: FirebaseAuth , private val firestore: FirebaseFirestore) : AuthenticationDataSource {
    override fun login(email: String, pass: String): Maybe<AuthResult> {
        return RxFirebaseAuth.signInWithEmailAndPassword(mAuth , email , pass)
    }

    override fun signup(user: User , pass: String): Maybe<AuthResult> {
       return Maybe.create { emmit ->
            mAuth.createUserWithEmailAndPassword(user.email!! , pass).addOnSuccessListener { auth->
                firestore.collection(USERS_COLLECTION).document(auth.user!!.uid).set(user)
                    .addOnSuccessListener { emmit.onSuccess(auth) }
                    .addOnFailureListener { emmit.onError(Throwable("Error in saving user data")) }
            }.addOnFailureListener { emmit.onError(Throwable(it.localizedMessage)) }
        }
    }
}