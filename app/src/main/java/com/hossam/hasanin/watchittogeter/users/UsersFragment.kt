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
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.hossam.hasanin.base.navigationController.NavigationManager
import com.hossam.hasanin.watchittogeter.R
import com.hossam.hasanin.base.externals.onEndReachedStaggerdLayout
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
            if (it.currentRoomId.isNullOrEmpty()){
                popUpGetRoomData(it.id!!)
            } else {
                Toast.makeText(requireContext() , "This user is inside a room" , Toast.LENGTH_LONG).show()
            }
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
                data.putString("roomId" , it.roomCreatedId)
                Log.v("lolo" , it.roomCreatedId)
                navigationManager.navigateTo(NavigationManager.WATCH_ROOM , data , requireActivity())
            }

            if (it.createRoomError != null){
                Toast.makeText(requireContext() , it.createRoomError.localizedMessage , Toast.LENGTH_LONG).show()
                viewModel.clearStates()
            }

            if (it.addContactError != null){
                Toast.makeText(requireContext() , it.addContactError.localizedMessage , Toast.LENGTH_LONG).show()
                viewModel.clearStates()
            }

            if (it.users.isNotEmpty() || viewModel.cashedList.isNotEmpty()){
                tv_error_mess.visibility = View.GONE
                if (!viewModel.cashedList.contains(it.users[0]) && !viewModel.cashedList.contains(it.users[it.users.lastIndex])) {
                    viewModel.cashedList.addAll(it.users)
                    usersAdapter.submitList(viewModel.cashedList)
                }
            } else {
                tv_error_mess.visibility = View.VISIBLE
                tv_error_mess.text = "You can add contacts throw +"
            }
        }

        user_rec.layoutManager = StaggeredGridLayoutManager(4 , StaggeredGridLayoutManager.VERTICAL)
        user_rec.adapter = usersAdapter

        user_rec.onEndReachedStaggerdLayout {
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
            val watchRoom = WatchRoom(id = System.currentTimeMillis().toString(), name = roomName
                , mp4Url = mp4Url , users = arrayListOf(viewModel.currentUser.uid , userId) , roomId = null)
            viewModel.createRoom(watchRoom)
            ad.dismiss()
        }

        ad.show()
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