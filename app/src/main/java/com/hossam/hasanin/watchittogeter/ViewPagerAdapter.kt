package com.hossam.hasanin.watchittogeter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.hossam.hasanin.watchittogeter.users.UsersFragment
import com.hossam.hasanin.watchittogeter.users.UsersViewModel
import com.hossam.hasanin.watchittogeter.watchRooms.WatchRoomsFragment
import com.hossam.hasanin.watchittogeter.watchRooms.WatchRoomsViewModel

class ViewPagerAdapter(ac: FragmentActivity): FragmentStateAdapter(ac) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                UsersFragment()
            }
            1 -> {
                WatchRoomsFragment()
            }
            else -> {
                throw Exception("No fragment exist")
            }
        }
    }
}