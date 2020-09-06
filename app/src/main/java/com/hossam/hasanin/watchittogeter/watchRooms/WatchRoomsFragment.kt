package com.hossam.hasanin.watchittogeter.watchRooms

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.hossam.hasanin.watchittogeter.R
import com.hossam.hasanin.base.externals.onEndReachedLinearLayout
import com.hossam.hasanin.base.models.WatchRoom
import com.hossam.hasanin.base.navigationController.NavigationManager
import com.hossam.hasanin.watchittogeter.users.UserWrapper
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.room_info_pop_up.view.*
import kotlinx.android.synthetic.main.users_fragment.loading
import kotlinx.android.synthetic.main.users_fragment.tv_error_mess
import kotlinx.android.synthetic.main.watch_rooms_fragment.*
import javax.inject.Inject

@AndroidEntryPoint
class WatchRoomsFragment : Fragment() {


    private val viewModel by viewModels<WatchRoomsViewModel>()
    lateinit var disposable: Disposable
    @Inject lateinit var roomAdapter: RoomsAdapter
    @Inject lateinit var navigationManager: NavigationManager
    var loadingDialog: AlertDialog? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.watch_rooms_fragment, container, false)
    }

    @ExperimentalStdlibApi
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        roomAdapter.doAction = {
           // Go to the room if still available

            if (it.state == WatchRoom.PREPARING) {
                navigationManager.navigateTo(NavigationManager.WATCH_ROOM , Bundle() , requireActivity())
            } else if (it.state == WatchRoom.RUNNING) {
                goTo(R.id.playFragment)
            }else {
                Toast.makeText(requireContext() , "Well unfortunatlly this room is unavailable" , Toast.LENGTH_LONG).show()
            }
        }

        disposable = viewModel.viewState().observeOn(AndroidSchedulers.mainThread()).subscribe {
            Log.v("koko" , it.toString())
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

            if (!it.loadingMore){
                val list = viewModel.cashedList.toMutableList()
                if (list.isNotEmpty()){
                    if (list[list.lastIndex].type == UserWrapper.LOADING){
                        list.removeLast()
                    }
                }
            }

            if (!it.refresh){
                refresh_layout.isRefreshing = false
            }

            if (it.searchingRoom){
                // show loading bar
                loadingDialog!!.show()
            }

            if (it.searchedRoom != null){
                // disapeare loading dialog
                loadingDialog!!.dismiss()
                // show enter room pop up
                popUpWatchRoomInfo(it.searchedRoom)
            }

            if (it.enteringTheRoom){
                loading.visibility = View.VISIBLE
            } else {
                loading.visibility = View.GONE
            }

            if (it.enteredRoomState != null){
                when(it.enteredRoomState){
                    WatchRoom.PREPARING->{
                        // go to group fragment
                        goTo(R.id.groupFragment)
                    }
                    WatchRoom.RUNNING->{
                        // go to playing fragment
                        goTo(R.id.playFragment)
                    }
                }
            }

            if (it.errorEntering != null){
                Toast.makeText(requireContext() , it.errorEntering.localizedMessage , Toast.LENGTH_LONG).show()
            }

            if (it.rooms.isNotEmpty()){
//                if (!viewModel.cashedList.contains(it.rooms[0]) && !viewModel.cashedList.contains(it.rooms[it.rooms.lastIndex])) {
//                    viewModel.cashedList.addAll(it.rooms)
//                }
                viewModel.cashedList.addAll(it.rooms)
                roomAdapter.submitList(viewModel.cashedList)
            }
        }

        rooms_rec.layoutManager = LinearLayoutManager(requireContext())
        rooms_rec.adapter = roomAdapter

        refresh_layout.setOnRefreshListener {
            viewModel.refresh()
        }

        rooms_rec.onEndReachedLinearLayout {
            viewModel.loadMore()
        }

        btn_search.setOnClickListener {
            val roomId = et_search.text.toString()
            viewModel.searchFor(roomId)
        }

    }

    override fun onStart() {
        super.onStart()
        popUpProgressBar()
    }


    private fun popUpProgressBar(){
        val d = AlertDialog.Builder(requireContext())
        val layout = LayoutInflater.from(requireContext()).inflate(R.layout.loading , null)
        d.setView(layout)
        d.setCancelable(false)
        loadingDialog = d.create()
    }

    private fun popUpWatchRoomInfo(room: WatchRoom){
        val d = AlertDialog.Builder(requireContext())
        val layout = LayoutInflater.from(requireContext()).inflate(R.layout.room_info_pop_up , null)
        d.setView(layout)
        val ad = d.create()
        layout.tv_name.text = room.name
        layout.tv_desc.text = room.desc
        layout.tv_state.text = when(room.state){
            WatchRoom.PREPARING ->{
                "In the steady room"
            }
            WatchRoom.RUNNING ->{
                "Playing the video"
            }
            WatchRoom.FINISHED ->{
                "The room is not available , it is finished"
            }
            else -> {
                throw Exception("No such a state")
            }
        }
        d.setPositiveButton("Enter >"){ dialog: DialogInterface?, which: Int ->
            // enter the room
            if (room.state != WatchRoom.FINISHED){
                viewModel.enteringRoom(room.id , room.state!!)
            } else {
                Toast.makeText(requireContext() , "The room is not available , it is finished" , Toast.LENGTH_LONG).show()
            }
            ad.dismiss()
        }
        ad.show()

    }

    private fun goTo(dist: Int){
        val bundle = Bundle()
        bundle.putInt("goTo" , dist)
        navigationManager.navigateTo(NavigationManager.WATCH_ROOM , bundle , requireActivity())
    }

    override fun onDestroy() {
        disposable.dispose()
        super.onDestroy()
    }

}