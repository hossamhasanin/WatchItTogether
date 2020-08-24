package com.hossam.hasanin.base.models

import androidx.recyclerview.widget.DiffUtil
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ServerTimestamp
import com.google.firestore.v1.DocumentTransform

@Entity(tableName = "users")
data class User (
    @PrimaryKey(autoGenerate = true)
    val roomId: Int?,
    val id: String? = "",
    val name: String = "",
    val email: String? = "",
    val phone: String? = "",
    val gender: Int? = null,
    val currentRoomId: String? = "",
    val addedBy: ArrayList<String> = arrayListOf(),
    val lastSeen: Timestamp? = null,
    val createdAt: Timestamp? = null
){

    constructor(): this(null , null , "" , null , null , null , null , arrayListOf<String>() , null , null)

    companion object : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {

            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.name.equals(newItem.name) || oldItem.email.equals(newItem.email)
                    || oldItem.phone.equals(newItem.phone) || oldItem.gender == newItem.gender
                    || oldItem.currentRoomId.equals(newItem.currentRoomId)
        }
        var current: User? = null
    }
}