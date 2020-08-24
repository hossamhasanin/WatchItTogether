package com.hossam.hasanin.base.models

import androidx.recyclerview.widget.DiffUtil

data class UserState (
    val id: String = "",
    val name: String = "",
    val gender: Int? = null,
    var state: Int? = null,
    val leader: Boolean = false,
    val videoPosition: Long? = null
){

    constructor(): this("" , "" , null , null , false , null)

    companion object {
        const val ENTERED = 0
        const val READY = 1
        const val PLAYING = 2
        const val PAUSE = 3
        const val FINISHED = 4

        val diffUtil = object : DiffUtil.ItemCallback<UserState>() {

            override fun areItemsTheSame(oldItem: UserState, newItem: UserState): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: UserState, newItem: UserState): Boolean {
                return oldItem.state == newItem.state || oldItem.videoPosition == newItem.videoPosition
            }
        }
    }

    fun toMap(): MutableMap<String , Any> {
        return mutableMapOf("id" to id
            , "name" to name
            , "gender" to gender as Any
            , "state" to state as Any
            , "leader" to leader
            , "videoPosition" to videoPosition as Any)
    }
}