package com.hossam.hasanin.watchittogeter.models

import androidx.recyclerview.widget.DiffUtil

data class User (
    val id: String? = "",
    val name: String? = "",
    val email: String? = "",
    val phone: String? = "",
    val gender: Int? = null
){
    companion object : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.name.equals(newItem.name) || oldItem.email.equals(newItem.email)
                    || oldItem.phone.equals(newItem.phone) || oldItem.gender == newItem.gender
        }
    }
}