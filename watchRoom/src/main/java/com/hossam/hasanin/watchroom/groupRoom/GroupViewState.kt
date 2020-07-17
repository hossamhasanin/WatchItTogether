package com.hossam.hasanin.watchroom.groupRoom

import com.hossam.hasanin.watchittogeter.models.UserState
import java.lang.Exception

data class GroupViewState (
    val users: List<UserStateWrapper>,
    val loading: Boolean,
    val error: Exception?,
    val roomSate: Int
)