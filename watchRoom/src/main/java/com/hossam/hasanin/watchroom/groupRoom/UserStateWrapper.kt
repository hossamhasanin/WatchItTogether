package com.hossam.hasanin.watchroom.groupRoom

import androidx.recyclerview.widget.DiffUtil
import com.hossam.hasanin.base.models.UserState

data class UserStateWrapper (
    val userState: UserState?,
    val type: Int
){
    companion object{
        val CONTENT = 0
        val LOADING = 1

        val diffUtil = object : DiffUtil.ItemCallback<UserStateWrapper>() {
            override fun areItemsTheSame(oldItem: UserStateWrapper, newItem: UserStateWrapper): Boolean {
                return oldItem.type == newItem.type
            }

            override fun areContentsTheSame(oldItem: UserStateWrapper, newItem: UserStateWrapper): Boolean {
                return UserState.diffUtil.areContentsTheSame(oldItem.userState ?: UserState() , newItem.userState ?: UserState())
            }
        }
    }
}