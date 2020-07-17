package com.hossam.hasanin.watchittogeter.models

import androidx.recyclerview.widget.DiffUtil

data class UserState (
    val id: String = "",
    val state: Int? = null,
    val videoPosition: Long? = null
){
    companion object : DiffUtil.ItemCallback<UserState>() {
        override fun areItemsTheSame(oldItem: UserState, newItem: UserState): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: UserState, newItem: UserState): Boolean {
            return oldItem.state == newItem.state || oldItem.videoPosition == newItem.videoPosition
        }
        var current: User? = null
    }
}