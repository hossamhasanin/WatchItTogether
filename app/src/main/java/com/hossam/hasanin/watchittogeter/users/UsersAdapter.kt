package com.hossam.hasanin.watchittogeter.users

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.avatarfirst.avatargenlib.AvatarConstants
import com.avatarfirst.avatargenlib.AvatarGenerator
import com.bumptech.glide.Glide
import com.hossam.hasanin.watchittogeter.R
import com.hossam.hasanin.base.models.User
import kotlinx.android.synthetic.main.user_card.view.*
import java.text.DateFormat
import javax.inject.Inject

class UsersAdapter @Inject constructor():
    ListAdapter<UserWrapper, UsersAdapter.ViewHolder>(UserWrapper.diffUtil) {

    var doAction: (User) -> Unit = {}

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
        abstract fun onBind(pos: Int, userWrapper: UserWrapper , doAction: (User) -> Unit)
    }

    class LoadingViewHolder(view: View): ViewHolder(view){
        override fun onBind(pos: Int, userWrapper: UserWrapper , doAction: (User) -> Unit) {

        }
    }

    class UserViewHolder(view: View) : ViewHolder(view){
        private val name = view.name
        private val img = view.iv_user_image
        private val userCont = view.cont_user
        private val tvLastSeen = view.tv_last_seen

        override fun onBind(pos: Int, userWrapper: UserWrapper , doAction: (User) -> Unit) {
            name.text = userWrapper.user!!.name
            Glide.with(img.context)
                .load(AvatarGenerator.avatarImage(img.context, 100, AvatarConstants.CIRCLE, userWrapper.user!!.name))
                .into(img)
            val dateFormat = DateFormat.getInstance().format(userWrapper.user.createdAt!!.toDate())
            tvLastSeen.text = "Last seen $dateFormat"
            userCont.setOnClickListener {
                doAction(userWrapper.user)
            }
        }

    }

}