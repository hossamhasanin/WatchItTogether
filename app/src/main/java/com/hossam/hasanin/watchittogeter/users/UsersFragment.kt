package com.hossam.hasanin.watchittogeter.users

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
import com.hossam.hasanin.base.externals.onEndReachedLinearLayout
import com.hossam.hasanin.base.navigationController.NavigationManager
import com.hossam.hasanin.watchittogeter.R
import com.hossam.hasanin.base.models.WatchRoom
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.add_contacts.view.*
import kotlinx.android.synthetic.main.get_watch_room_data.view.*
import kotlinx.android.synthetic.main.users_fragment.*
import javax.inject.Inject

@AndroidEntryPoint
class UsersFragment : Fragment() {


    private val viewModel by viewModels<UsersViewModel>()
    lateinit var disposable: Disposable
    @Inject lateinit var usersAdapter: UsersAdapter
    @Inject lateinit var navigationManager: NavigationManager

    var loadingDialog: AlertDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.users_fragment, container, false)
    }

    @ExperimentalStdlibApi
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        usersAdapter.doAction = {
            viewModel.updateContact(it.id!!)
        }

        disposable = viewModel.viewState().observeOn(AndroidSchedulers.mainThread()).subscribe {
            Log.v("lolo" , it.toString())
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

            if (it.roomCreated){
                Toast.makeText(requireContext() , "The room created successfully." , Toast.LENGTH_LONG).show()
                // go to the room
                val data = Bundle()
                data.putParcelable("room" , it.roomCreatedObject)
                data.putBoolean("leader" , true)
                Log.v("lolo" , it.roomCreatedObject.toString())
                navigationManager.navigateTo(NavigationManager.WATCH_ROOM , data , requireActivity())
            }

            if (it.createRoomError != null){
                Toast.makeText(requireContext() , it.createRoomError.localizedMessage , Toast.LENGTH_LONG).show()
                viewModel.clearStates()
            }

            if (it.addContactError != null){
                Toast.makeText(requireContext() , it.addContactError.localizedMessage , Toast.LENGTH_LONG).show()
               // viewModel.clearStates()
            }

            if (it.updatingContactData) {
                // open loading bar
                popupProgressBar()
            }

            if (it.updateContactData != null && !it.creatingRoom){
                if (loadingDialog!!.isShowing){
                    loadingDialog!!.dismiss()
                }
                if (it.updateContactData.currentRoomId == ""){
                    popUpGetRoomData(it.updateContactData!!.id!!)
                } else {
                    Toast.makeText(requireContext() , "${it.updateContactData.name} is in a room" , Toast.LENGTH_LONG).show()
                    viewModel.clearStates()
                }
            }

            if (it.users.isNotEmpty() || viewModel.cashedList.isNotEmpty()){
                tv_error_mess.visibility = View.GONE
                if (!viewModel.cashedList.contains(it.users[0]) && !viewModel.cashedList.contains(it.users[it.users.lastIndex])) {
                    viewModel.cashedList.addAll(it.users)
                    usersAdapter.submitList(viewModel.cashedList)
                }
            } else {
                tv_error_mess.visibility = View.VISIBLE
                tv_error_mess.text = "You don't have any contacts yet ."
            }
        }

        user_rec.layoutManager = LinearLayoutManager(requireContext())
        user_rec.adapter = usersAdapter

        user_rec.onEndReachedLinearLayout {
            viewModel.loadMore()
        }

        addContacts.setOnClickListener {
            popUpAddNewContact()
        }

    }

    private fun popUpGetRoomData(userId: String){
        val dialog = AlertDialog.Builder(requireContext())
        val layout = LayoutInflater.from(requireContext()).inflate(R.layout.get_watch_room_data , null)
        dialog.setView(layout)
        val ad = dialog.create()

        layout.btn_create.setOnClickListener {
            val roomName = layout.tv_room_name.text.toString()
            val mp4Url = layout.tv_mp4_url.text.toString()
            val desc = layout.tv_room_desc.text.toString()
            val watchRoom = WatchRoom(id = System.currentTimeMillis().toString(), name = roomName , desc = desc
                , mp4Url = mp4Url , users = arrayListOf(viewModel.currentUser.uid , userId) ,
                state = WatchRoom.PREPARING , roomId = null)
            viewModel.createRoom(watchRoom)
            ad.dismiss()
        }

        ad.show()
    }

    private fun popupProgressBar(){
        val d = AlertDialog.Builder(requireContext())
        val layout = LayoutInflater.from(requireContext()).inflate(R.layout.loading , null)
        d.setView(layout)
        d.setCancelable(false)
        loadingDialog = d.create()
        loadingDialog!!.show()
    }

    private fun popUpAddNewContact(){
        val dialog = AlertDialog.Builder(requireContext())
        val layout = LayoutInflater.from(requireContext()).inflate(R.layout.add_contacts , null)
        dialog.setView(layout)
        val ad = dialog.create()
        layout.btn_add.setOnClickListener {
            val query = layout.et_phone_email.text.toString()
            viewModel.addContact(query)
            ad.dismiss()
        }
        ad.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }

}