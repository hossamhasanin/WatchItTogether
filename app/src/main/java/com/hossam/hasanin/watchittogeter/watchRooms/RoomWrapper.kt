package com.hossam.hasanin.watchittogeter.watchRooms

import androidx.recyclerview.widget.DiffUtil
import com.hossam.hasanin.watchittogeter.models.WatchRoom

data class RoomWrapper(
    val room: WatchRoom?,
    val type: Int
){
    companion object{
        val CONTENT = 0
        val LOADING = 1

        val diffUtil = object : DiffUtil.ItemCallback<RoomWrapper>() {
            override fun areItemsTheSame(oldItem: RoomWrapper, newItem: RoomWrapper): Boolean {
                return oldItem.type == newItem.type
            }

            override fun areContentsTheSame(oldItem: RoomWrapper, newItem: RoomWrapper): Boolean {
                return WatchRoom.areContentsTheSame(oldItem.room ?: WatchRoom() , newItem.room ?: WatchRoom())
            }
        }
    }
}