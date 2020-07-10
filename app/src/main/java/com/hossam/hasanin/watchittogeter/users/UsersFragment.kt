package com.hossam.hasanin.watchittogeter.users

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.firebase.firestore.FieldValue
import com.google.firestore.v1.DocumentTransform
import com.hossam.hasanin.watchittogeter.R
import com.hossam.hasanin.watchittogeter.externals.onEndReached
import com.hossam.hasanin.watchittogeter.models.User
import com.hossam.hasanin.watchittogeter.models.WatchRoom
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.get_watch_room_data.view.*
import kotlinx.android.synthetic.main.users_fragment.*
import javax.inject.Inject

@AndroidEntryPoint
class UsersFragment : Fragment() {


    private val viewModel by viewModels<UsersViewModel>()
    lateinit var disposable: Disposable
    @Inject lateinit var usersAdapter: UsersAdapter

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
            popUpGetRoomData(it)
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

            if (it.roomCreated){
                Toast.makeText(requireContext() , "The room created successfully." , Toast.LENGTH_LONG).show()
                viewModel.clearCreatingRoomStates()
            }

            if (it.users.isNotEmpty()){
                if (!viewModel.cashedList.contains(it.users[0]) && !viewModel.cashedList.contains(it.users[it.users.lastIndex])) {
                    viewModel.cashedList.addAll(it.users)
                    usersAdapter.submitList(viewModel.cashedList)
                }
            }
        }

        user_rec.layoutManager = StaggeredGridLayoutManager(4 , StaggeredGridLayoutManager.VERTICAL)
        user_rec.adapter = usersAdapter

        user_rec.onEndReached {
            viewModel.loadMore()
        }
    }

    private fun popUpGetRoomData(id: String){
        val dialog = AlertDialog.Builder(requireContext())
        val layout = LayoutInflater.from(requireContext()).inflate(R.layout.get_watch_room_data , null)
        dialog.setView(layout)
        val ad = dialog.create()

        layout.btn_create.setOnClickListener {
            val roomName = layout.tv_room_name.text.toString()
            val mp4Url = layout.tv_mp4_url.text.toString()
            val watchRoom = WatchRoom(id = System.currentTimeMillis().toString(), name = roomName , mp4Url = mp4Url)
            viewModel.createRoom(watchRoom)
            ad.dismiss()
        }

        ad.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }

}