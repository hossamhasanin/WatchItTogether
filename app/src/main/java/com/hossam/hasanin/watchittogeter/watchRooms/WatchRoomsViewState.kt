package com.hossam.hasanin.watchittogeter.watchRooms

import java.lang.Exception

data class WatchRoomsViewState(
    val rooms: List<RoomWrapper>,
    val loading: Boolean,
    val error: Exception?,
    val loadingMore: Boolean,
    val refresh: Boolean
)