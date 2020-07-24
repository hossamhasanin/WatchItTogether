package com.hossam.hasanin.watchroom.groupRoom

import com.hossam.hasanin.base.models.UserState
import java.lang.Exception

data class GroupViewState (
    val users: List<UserState>,
    val loading: Boolean,
    val error: Exception?,
    val roomSate: Int
)