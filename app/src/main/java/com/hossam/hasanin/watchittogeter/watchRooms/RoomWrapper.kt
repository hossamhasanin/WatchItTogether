package com.hossam.hasanin.watchittogeter.watchRooms

import androidx.recyclerview.widget.DiffUtil
import com.hossam.hasanin.base.models.WatchRoom

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
                return WatchRoom.diffUtil.areContentsTheSame(oldItem.room ?: WatchRoom(roomId = null) , newItem.room ?: WatchRoom(roomId = null))
            }
        }
    }
}