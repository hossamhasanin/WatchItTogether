package com.hossam.hasanin.watchroom.groupRoom

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.hossam.hasanin.watchroom.R
import com.hossam.hasanin.watchroom.WatchRoomActivity
import dagger.hilt.android.AndroidEntryPoint
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

        val roomId = (requireActivity() as WatchRoomActivity ).intent?.extras!!.getString("roomId")

        Log.v("soso" , "fragment here $roomId")

        viewModel.getUsers(roomId!!)

        viewModel.setCurrentUserState()

        disposable = viewModel.viewState().subscribe {
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

    }

}