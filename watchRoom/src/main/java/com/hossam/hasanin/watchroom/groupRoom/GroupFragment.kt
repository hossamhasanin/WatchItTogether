package com.hossam.hasanin.watchroom.groupRoom

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    lateinit var disposable: Disposable
    private val viewModel by viewModels<GroupViewModel>()
    @Inject lateinit var userStateAdapter: UserStateAdapter

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
        val isLeader = (requireActivity() as WatchRoomActivity ).intent?.extras!!.getBoolean("leader")

        displayRoomData(room)

        userStateAdapter.tellIfReady = {isReady ->
            if (isReady){
                // show play button but only to the leader
                btn_play.visibility = View.VISIBLE
            } else {
                // hide play button
                btn_play.visibility = View.GONE
            }
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
                loading.visibility = View.GONE
            }

            if (it.error != null){
                tv_error_mess.visibility = View.VISIBLE
                tv_error_mess.text = it.error.localizedMessage
            } else {
                tv_error_mess.visibility = View.GONE
            }

            if (it.users.isNotEmpty()){
//                viewModel.cashedUserList.addAll(it.users)
                userStateAdapter.submitList(it.users)
                userStateAdapter.notifyDataSetChanged()
                Log.v("soso" , "viewState ${it.users}")
            }

        }

        rec_users.layoutManager = LinearLayoutManager(requireContext())
        rec_users.adapter = userStateAdapter

        btn_play.setOnClickListener {
            findNavController().navigate(R.id.playFragment)
        }

        btn_ready.setOnClickListener {
            viewModel.currentUserState.state = UserState.READY
            viewModel.updateUserState()
        }

    }

    private fun displayRoomData(roomData: WatchRoom){
        tv_movie_name.text = roomData.name
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.leaveRoom()
        disposable.dispose()
    }

}