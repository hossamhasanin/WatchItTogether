package com.hossam.hasanin.base.models

import androidx.recyclerview.widget.DiffUtil
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watchroom")
data class WatchRoom (
    @PrimaryKey(autoGenerate = true)
    val roomId: Int?,
    val id: String = "",
    val name: String = "",
    val mp4Url: String = "",
    val state: Int? = null,
    val users: ArrayList<String> = arrayListOf()
){

    constructor(): this(null , "" , "" , "" , null , arrayListOf())

    companion object {
        const val READY = 0
        const val PLAYING = 1
        const val PAUSE = 2
        const val FINISHED = 3

        val diffUtil = object : DiffUtil.ItemCallback<WatchRoom>() {

            override fun areItemsTheSame(oldItem: WatchRoom, newItem: WatchRoom): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: WatchRoom, newItem: WatchRoom): Boolean {
                return oldItem.name.equals(newItem.name) || oldItem.mp4Url.equals(newItem.mp4Url)
                        || oldItem.users.containsAll(newItem.users)
            }
        }
    }
}