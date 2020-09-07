package com.hossam.hasanin.watchittogeter.watchRooms

import com.hossam.hasanin.base.models.WatchRoom
import java.lang.Exception

data class WatchRoomsViewState(
    val rooms: List<RoomWrapper>,
    val loading: Boolean,
    val error: Exception?,
    val loadingMore: Boolean,
    val refresh: Boolean,
    val searchedRoom: WatchRoom?,
    val searchingRoom: Boolean,
    val searchError: Exception?,
    val enteringTheRoom: Boolean,
    val enteredRoom: WatchRoom?,
    val errorEntering: Exception?
)