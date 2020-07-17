package com.hossam.hasanin.watchroom.playRoom

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.hossam.hasanin.watchroom.R
import com.hossam.hasanin.watchroom.groupRoom.GroupViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlayFragment : Fragment() {

    private val viewModel by viewModels<PlayViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.play_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

}