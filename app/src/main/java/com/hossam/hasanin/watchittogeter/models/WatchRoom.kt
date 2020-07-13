package com.hossam.hasanin.watchittogeter.models

import androidx.recyclerview.widget.DiffUtil


data class WatchRoom (
    val id: String = "",
    val name: String = "",
    val mp4Url: String = "",
    val state: Int? = null,
    val users: ArrayList<String> = arrayListOf()
){
    companion object : DiffUtil.ItemCallback<WatchRoom>() {

        const val READY = 0
        const val PLAYING = 1
        const val PAUSE = 2
        const val FINISHED = 3


        override fun areItemsTheSame(oldItem: WatchRoom, newItem: WatchRoom): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: WatchRoom, newItem: WatchRoom): Boolean {
            return oldItem.name.equals(newItem.name) || oldItem.mp4Url.equals(newItem.mp4Url)
                    || oldItem.users.containsAll(newItem.users)
        }
    }
}