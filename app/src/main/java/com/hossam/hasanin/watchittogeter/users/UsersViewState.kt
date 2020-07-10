package com.hossam.hasanin.watchittogeter.users

import java.lang.Exception

data class UsersViewState(
    val users: List<UserWrapper>,
    val loading: Boolean,
    val error: Exception?,
    val loadingMore: Boolean,
    val refresh: Boolean,
    val creatingRoom: Boolean,
    val roomCreated: Boolean
)