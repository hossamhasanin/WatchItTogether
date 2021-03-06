package com.hossam.hasanin.watchittogeter.watchRooms

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hossam.hasanin.watchittogeter.R
import com.hossam.hasanin.base.models.WatchRoom
import kotlinx.android.synthetic.main.room_card.view.*
import javax.inject.Inject

class RoomsAdapter@Inject constructor():
    ListAdapter<RoomWrapper, RoomsAdapter.ViewHolder>(RoomWrapper.diffUtil) {

    var doAction: (WatchRoom) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when(viewType){
            RoomWrapper.CONTENT -> {
                UserViewHolder(layoutInflater.inflate(R.layout.room_card , parent , false))
            }
            RoomWrapper.LOADING -> {
                LoadingViewHolder(layoutInflater.inflate(R.layout.loading , parent , false))
            }
            else -> {
                throw IllegalStateException("Not allowed type")
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(position , getItem(position) , doAction)
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).type
    }

    abstract class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        abstract fun onBind(pos: Int, roomWrapper: RoomWrapper, doAction: (WatchRoom) -> Unit)
    }

    class LoadingViewHolder(view: View): ViewHolder(view){
        override fun onBind(pos: Int, roomWrapper: RoomWrapper, doAction: (WatchRoom) -> Unit) {

        }
    }

    class UserViewHolder(view: View) : ViewHolder(view){
        private val name = view.tv_room_name
        private val state = view.tv_room_state
        private val roomCont = view.room_cont

        override fun onBind(pos: Int, roomWrapper: RoomWrapper, doAction: (WatchRoom) -> Unit) {
            name.text = roomWrapper.room?.name
            state.text = when(roomWrapper.room?.state){
                WatchRoom.RUNNING -> {
                    "The video is playing now without you , you are missing a lot !"
                }
                WatchRoom.PREPARING -> {
                    "In the grouping room yet ."
                }
                WatchRoom.FINISHED -> {
                    "Well , bal the room ended ."
                }
                else -> {
                    throw Exception("No such state")
                }
            }

            roomCont.setOnClickListener {
                doAction(roomWrapper.room)
            }

        }

    }


}