package com.hossam.hasanin.watchittogeter.watchRooms

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.hossam.hasanin.watchittogeter.R
import com.hossam.hasanin.base.externals.onEndReachedLinearLayout
import com.hossam.hasanin.watchittogeter.users.UserWrapper
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.users_fragment.*
import kotlinx.android.synthetic.main.users_fragment.loading
import kotlinx.android.synthetic.main.users_fragment.tv_error_mess
import kotlinx.android.synthetic.main.watch_rooms_fragment.*
import javax.inject.Inject

@AndroidEntryPoint
class WatchRoomsFragment : Fragment() {


    private val viewModel by viewModels<WatchRoomsViewModel>()
    lateinit var disposable: Disposable
    @Inject lateinit var roomAdapter: RoomsAdapter


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

            if (it.rooms.isNotEmpty()){
                if (!viewModel.cashedList.contains(it.rooms[0]) && !viewModel.cashedList.contains(it.rooms[it.rooms.lastIndex])) {
                    viewModel.cashedList.addAll(it.rooms)
                    roomAdapter.submitList(viewModel.cashedList)
                }
            }
        }

        rooms_rec.layoutManager = LinearLayoutManager(requireContext())
        rooms_rec.adapter = roomAdapter

        user_rec.onEndReachedLinearLayout {
            viewModel.loadMore()
        }

    }

}