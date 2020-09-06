package com.hossam.hasanin.watchroom.playRoom

import com.hossam.hasanin.base.models.UserState
import java.lang.Exception

data class PlayViewState(
    val users: List<UserState>,
    val loading: Boolean,
    val error: Exception?,
    val roomSate: Int,
    val videoUrl: String?,
    val showUsersState: Boolean,
    val showOptions: Boolean
)