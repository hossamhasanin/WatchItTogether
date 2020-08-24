package com.hossam.hasanin.base.models

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "watchroom")
@Parcelize
data class WatchRoom (
    @PrimaryKey(autoGenerate = true)
    val roomId: Int?,
    val id: String = "",
    val name: String = "",
    val desc: String = "",
    val mp4Url: String = "",
    val state: Int? = null,
    val users: ArrayList<String> = arrayListOf(),
    val createdAt: Timestamp? = null
): Parcelable{

    constructor(): this(null , "" , "" , "" , "" , null , arrayListOf() , null)

    companion object {
        const val PREPARING = 0
        const val RUNNING = 1
        const val FINISHED = 2

        val diffUtil = object : DiffUtil.ItemCallback<WatchRoom>() {

            override fun areItemsTheSame(oldItem: WatchRoom, newItem: WatchRoom): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: WatchRoom, newItem: WatchRoom): Boolean {
                return oldItem.name.equals(newItem.name) || oldItem.mp4Url.equals(newItem.mp4Url)
                        || oldItem.users.containsAll(newItem.users)
            }
        }
    }
}