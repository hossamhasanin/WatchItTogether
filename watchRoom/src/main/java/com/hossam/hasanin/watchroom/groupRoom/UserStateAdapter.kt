package com.hossam.hasanin.watchroom.groupRoom

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hossam.hasanin.base.models.UserState
import com.hossam.hasanin.watchroom.R
import kotlinx.android.synthetic.main.user_state_card.view.*
import java.lang.Exception
import javax.inject.Inject

class UserStateAdapter @Inject constructor():
    ListAdapter<UserState, UserStateAdapter.ViewHolder>(UserState.diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return UserViewHolder(layoutInflater.inflate(R.layout.user_state_card , parent , false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(position , getItem(position))
    }

    abstract class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        abstract fun onBind(pos: Int, userState: UserState)
    }

//    class LoadingViewHolder(view: View): ViewHolder(view){
//        override fun onBind(pos: Int, userStateWrapper: UserStateWrapper) {
//
//        }
//    }

    class UserViewHolder(view: View) : ViewHolder(view){
        private val name = view.tv_username
        private val img = view.iv_user_image
        private val userStateCont = view.tv_user_state

        override fun onBind(pos: Int, userState: UserState) {
            name.text = userState.name
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
                UserState.FINISHED ->{
                    "Finished"
                }
                else -> {
                    throw Exception("No such a state")
                }
            }

        }

    }

}