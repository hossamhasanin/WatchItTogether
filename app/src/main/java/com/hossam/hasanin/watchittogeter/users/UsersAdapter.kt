package com.hossam.hasanin.watchittogeter.users

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hossam.hasanin.watchittogeter.R
import kotlinx.android.synthetic.main.user_card.view.*
import kotlinx.android.synthetic.main.users_fragment.view.*
import javax.inject.Inject

class UsersAdapter @Inject constructor():
    ListAdapter<UserWrapper, UsersAdapter.ViewHolder>(UserWrapper.diffUtil) {

    var doAction: (String) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when(viewType){
            UserWrapper.CONTENT -> {
                UserViewHolder(layoutInflater.inflate(R.layout.user_card , parent , false))
            }
            UserWrapper.LOADING -> {
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
        abstract fun onBind(pos: Int, userWrapper: UserWrapper , doAction: (String) -> Unit)
    }

    class LoadingViewHolder(view: View): ViewHolder(view){
        override fun onBind(pos: Int, userWrapper: UserWrapper , doAction: (String) -> Unit) {

        }
    }

    class UserViewHolder(view: View) : ViewHolder(view){
        private val name = view.name
        private val img = view.iv_user_image
        private val userCont = view.cont_user

        override fun onBind(pos: Int, userWrapper: UserWrapper , doAction: (String) -> Unit) {
            name.text = userWrapper.user!!.name
            if (userWrapper.user.gender == 0){
                Glide.with(img.context).load(R.drawable.female).into(img)
            } else {
                Glide.with(img.context).load(R.drawable.male).into(img)
            }
            userCont.setOnClickListener {
                doAction(userWrapper.user.id!!)
            }
        }

    }

}