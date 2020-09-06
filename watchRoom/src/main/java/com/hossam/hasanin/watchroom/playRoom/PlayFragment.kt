package com.hossam.hasanin.watchroom.playRoom

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.hossam.hasanin.base.models.UserState
import com.hossam.hasanin.base.models.WatchRoom
import com.hossam.hasanin.watchroom.R
import com.hossam.hasanin.watchroom.UserStateAdapter
import com.hossam.hasanin.watchroom.WatchRoomActivity
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.play_fragment.*
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class PlayFragment : Fragment() {

    private val viewModel by viewModels<PlayViewModel>()

    var player : SimpleExoPlayer? = null
    var room: WatchRoom? = null
    lateinit var mediaFactory : ProgressiveMediaSource.Factory
    @Inject lateinit var playbackStateListener: PlaybackStateListener
    @Inject lateinit var userStateAdapter: UserStateAdapter

    lateinit var disposable: Disposable

    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition: Long = 0
    private var job: Job? = null
    private var widthOfScreen: Float = 0f


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.play_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        val dataSourceFactory =
            DefaultDataSourceFactory(context, "roomPlayer")
        mediaFactory = ProgressiveMediaSource.Factory(dataSourceFactory)

        widthOfScreen = calculateWidthOfScreen()

        playbackStateListener.trackState = {
            when(it){
                ExoPlayer.STATE_IDLE -> {
                    // video gets prepared by the player
                    viewModel.updateUserState(UserState.READY, null)
                }
                ExoPlayer.STATE_READY -> {
                    // update the video position in the database

                    if (player!!.isPlaying) {
                        job = CoroutineScope(Dispatchers.IO).launch {
                            while (true) {
                                viewModel.updateUserState(
                                    UserState.PLAYING,
                                    player!!.contentPosition
                                )

                                delay(120000)
                            }
                        }
                    } else {
                        // update state the user stopped the video
                        stopSendingVideoPositionJob()
                        viewModel.updateUserState(UserState.PAUSE, null)
                    }
                }
                ExoPlayer.STATE_BUFFERING -> {
                    // update state the video stopped because of some problem
                    stopSendingVideoPositionJob()
                    viewModel.updateUserState(UserState.PAUSE, null)
                }
                ExoPlayer.STATE_ENDED -> {
                    // video finished
                    stopSendingVideoPositionJob()
                    viewModel.updateUserState(UserState.FINISHED, null)
                }
                else ->{

                }
            }
        }

        room = (requireActivity() as WatchRoomActivity).intent?.extras!!.getParcelable("room")
        val isLeader = (requireActivity() as WatchRoomActivity).intent?.extras!!.getBoolean("leader")

        viewModel.getUsers(room!!.id, isLeader)


        disposable = viewModel.viewState().observeOn(AndroidSchedulers.mainThread()).subscribe {
            if (it.loading){
                if (loading_states != null) {
                    loading_states.visibility = View.VISIBLE
                }
            } else {
                if (loading_states != null) {
                    loading_states.visibility = View.GONE
                }
            }
            if (it.error != null){
                Toast.makeText(requireContext(), it.error.localizedMessage, Toast.LENGTH_LONG).show()
            }
            if (it.users.isNotEmpty()){
                userStateAdapter.submitList(it.users.toMutableList())
                userStateAdapter.notifyDataSetChanged()
            }

            if (it.showUsersState){
                if (cont_users_rec != null){
                cont_users_rec.visibility = View.VISIBLE
                ObjectAnimator.ofFloat(cont_users_rec, "layout_width", -300f).apply {
                    duration = 700
                    start()
                }
                }
            } else {
                if (cont_users_rec != null) {
                    ObjectAnimator.ofFloat(cont_users_rec, "layout_width", 300f).apply {
                        duration = 700
                        start()
                    }
                    cont_users_rec.visibility = View.GONE
                }
                if (options != null) {
                    if (it.showOptions) {
                        options.visibility = View.VISIBLE
                    } else {
                        options.visibility = View.GONE
                    }
                }
            }

        }

        rec_user_state.layoutManager = LinearLayoutManager(requireContext())
        rec_user_state.adapter = userStateAdapter

        iv_open_users_state.setOnClickListener {
            viewModel.viewUserStateRec(true)
        }

        iv_cancle_users_rec.setOnClickListener {
            viewModel.viewUserStateRec(false)
        }

        iv_go_back.setOnClickListener {
            requireActivity().onBackPressed()
        }

        player_view.setOnClickListener {
            viewModel.optionsDisplay(!viewModel.viewStateValue().showOptions)
        }

    }

    private fun stopSendingVideoPositionJob(){
        if (job != null){
            job!!.cancel()
            job = null
        }
    }

    private fun initPlayer(){
        player = SimpleExoPlayer.Builder(requireContext()).build()
        player_view.player = player
        val mediaSource = mediaFactory.createMediaSource(Uri.parse(room!!.mp4Url))

        player!!.playWhenReady = playWhenReady
        player!!.seekTo(currentWindow, playbackPosition)
        player!!.addListener(playbackStateListener);
        player!!.prepare(mediaSource, false, false)
    }

    @SuppressLint("InlinedApi")
    private fun hideSystemUi() {
        player_view.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT >= 24) {
            initPlayer()
        }
    }

    override fun onResume() {
        super.onResume()
        hideSystemUi()
        if (Util.SDK_INT < 24 || player == null) {
            initPlayer()
        }
    }

    private fun releasePlayer() {
        if (player != null) {
            playWhenReady = player!!.playWhenReady
            playbackPosition = player!!.currentPosition
            currentWindow = player!!.currentWindowIndex
            player!!.release()
            player!!.removeListener(playbackStateListener);
            player = null
        }
    }

    private fun calculateWidthOfScreen(): Float{
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width: Int = displayMetrics.widthPixels

        return width.toFloat()
    }


    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT >= 24) {
            releasePlayer()
        }
        stopSendingVideoPositionJob()
        viewModel.updateUserState(UserState.PAUSE, null)
    }

    override fun onDestroy() {
        stopSendingVideoPositionJob()
//        viewModel.updateUserState(UserState.PAUSE, null)
        disposable.dispose()
        super.onDestroy()
    }

}