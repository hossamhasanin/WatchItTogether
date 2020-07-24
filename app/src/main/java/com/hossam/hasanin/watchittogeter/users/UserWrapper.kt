package com.hossam.hasanin.watchittogeter.users

import androidx.recyclerview.widget.DiffUtil
import com.hossam.hasanin.base.models.User

data class UserWrapper(
    val user: User?,
    val type: Int
){
    companion object{
        val CONTENT = 0
        val LOADING = 1

        val diffUtil = object : DiffUtil.ItemCallback<UserWrapper>() {
            override fun areItemsTheSame(oldItem: UserWrapper, newItem: UserWrapper): Boolean {
                return oldItem.type == newItem.type
            }

            override fun areContentsTheSame(oldItem: UserWrapper, newItem: UserWrapper): Boolean {
                return User.areContentsTheSame(oldItem.user ?: User(roomId = null) , newItem.user ?: User(roomId = null))
            }
        }
    }
}