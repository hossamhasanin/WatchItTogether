package com.hossam.hasanin.watchroom

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hossam.hasanin.base.models.User
import com.hossam.hasanin.base.models.UserState
import kotlinx.android.synthetic.main.user_state_card.view.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class UserStateAdapter @Inject constructor():
    ListAdapter<UserState, UserStateAdapter.ViewHolder>(UserState.diffUtil) {

    var tellIfReady: (Boolean) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return UserViewHolder(
            layoutInflater.inflate(R.layout.user_state_card, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(position, getItem(position), itemCount, tellIfReady)
    }

    var c = 1
    override fun submitList(list: MutableList<UserState>?) {
        Log.v("count", c.toString())
        c += 1
        super.submitList(list?.let { ArrayList(it) })
    }

    abstract class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        abstract fun onBind(
            pos: Int,
            userState: UserState,
            itemCount: Int,
            tellIfReady: (Boolean) -> Unit
        )
    }

//    class LoadingViewHolder(view: View): ViewHolder(view){
//        override fun onBind(pos: Int, userStateWrapper: UserStateWrapper) {
//
//        }
//    }

    class UserViewHolder(view: View) : ViewHolder(view){
        private val name = view.tv_username
        private val img = view.iv_user_image
        private val leader = view.tv_isleader
        private val videoPosition = view.tv_video_position
        private val userStateCont = view.tv_user_state

        var isReady = true

        override fun onBind(
            pos: Int,
            userState: UserState,
            itemCount: Int,
            tellIfReady: (Boolean) -> Unit
        ) {
            name.text = if (User.current!!.name == userState.name) "Me" else userState.name
            if (userState.gender == 0){
                Glide.with(img.context).load(R.drawable.female).into(img)
            } else {
                Glide.with(img.context).load(R.drawable.male).into(img)
            }

            userStateCont.text = when(userState.state){
                UserState.ENTERED -> {
                    "Entered"
                }
                UserState.READY -> {
                    "Ready"
                }
                UserState.PLAYING -> {
                    "Playing"
                }
                UserState.PAUSE -> {
                    "Pause"
                }
                UserState.FINISHED -> {
                    "Finished"
                }
                else -> {
                    throw Exception("No such a state")
                }
            }


            leader.text = if (userState.leader) "Leader" else "Member"
            videoPosition.text = "Position : ${getTime(userState.videoPosition!!)}"

            if (isReady){
                isReady = userState.state != UserState.PAUSE
            }

            if (pos == itemCount-1){
                tellIfReady(isReady)
            }

        }

        private fun getTime(position: Long): String? {
            val formatter = SimpleDateFormat("hh:mm:ss.SSS")

            val calendar = Calendar.getInstance()
            calendar.timeInMillis = position
            return formatter.format(position)
        }

    }

}