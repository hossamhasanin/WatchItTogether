package com.hossam.hasanin.watchroom.groupRoom

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.hossam.hasanin.base.models.UserState
import com.hossam.hasanin.base.models.WatchRoom
import com.hossam.hasanin.watchroom.R
import com.hossam.hasanin.watchroom.UserStateAdapter
import com.hossam.hasanin.watchroom.WatchRoomActivity
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.group_fragment.*
import javax.inject.Inject

@AndroidEntryPoint
class GroupFragment : Fragment() {

    var disposable: Disposable? = null
    private val viewModel by viewModels<GroupViewModel>()
    @Inject lateinit var userStateAdapter: UserStateAdapter
    var isLeader: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.group_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        val room: WatchRoom = (requireActivity() as WatchRoomActivity ).intent?.extras!!.getParcelable("room")!!
        isLeader = (requireActivity() as WatchRoomActivity ).intent?.extras!!.getBoolean("leader")

        displayRoomData(room)


        Log.v("soso" , "leader is $isLeader")

        userStateAdapter.tellIfReady = {isReady ->
            if (isReady && isLeader){
                // show play button but only to the leader
                btn_play.visibility = View.VISIBLE
            } else {
                // hide play button
                btn_play.visibility = View.GONE
            }
        }

        if (isLeader){
            // show play button but only to the leader
            btn_play.visibility = View.VISIBLE
        } else {
            // hide play button
            btn_play.visibility = View.GONE
        }

        Log.v("soso" , "fragment here $room")
        Log.v("soso" , "fragment userState ${viewModel.currentUserState}")

        viewModel.getUsers(room.id!! , isLeader)

        viewModel.initCurrentUserState()

        disposable = viewModel.viewState().observeOn(AndroidSchedulers.mainThread()).subscribe {
            Log.v("dodo" , it.toString())
            if (it.loading){
                loading.visibility = View.VISIBLE
            } else {
                if (loading != null){
                    loading.visibility = View.GONE
                }
            }

            if (it.error != null){
                tv_error_mess.visibility = View.VISIBLE
                tv_error_mess.text = it.error.localizedMessage
            } else {
                if (tv_error_mess != null) {
                    tv_error_mess.visibility = View.GONE
                }
            }

            when(it.roomSate){
                WatchRoom.RUNNING -> {
                    // go to the play room fragment
                    findNavController().navigate(R.id.playFragment)
                }
            }

            // i set the button to not clickable while actually send request to the server to update the state
            // so that the user cannot request it several time in the same time
            if (btn_play != null) {
                btn_play.isClickable = !it.updatingRoomState
            }


            if (it.users.isNotEmpty()){
                userStateAdapter.submitList(it.users.toMutableList())
                //userStateAdapter.notifyDataSetChanged()
                Log.v("soso" , "viewState ${it.users}")
            }
        }

        rec_users.layoutManager = LinearLayoutManager(requireContext())
        rec_users.adapter = userStateAdapter

        btn_play.setOnClickListener {
            if (userStateAdapter.currentList.size > 1) {
                viewModel.updateRoomState(WatchRoom.RUNNING)
            } else {
                Toast.makeText(requireContext() , "You can not enter alone !" , Toast.LENGTH_LONG).show()
            }
        }

        btn_ready.setOnClickListener {
            viewModel.currentUserState.state = UserState.READY
            viewModel.addOrUpdateUserState(true)
        }

    }

    private fun displayRoomData(roomData: WatchRoom){
        tv_movie_name.text = roomData.name
    }

    override fun onDestroy() {
        if (disposable != null) {
            disposable!!.dispose()
        }
        super.onDestroy()
    }

}