package com.hossam.hasanin.authentication.login

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.hossam.hasanin.authentication.R
import com.hossam.hasanin.base.navigationController.NavigationManager
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.login_fragment.*
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {

    lateinit var disposable: Disposable
    private val viewModel by viewModels<LoginViewModel>()
    @Inject lateinit var navigationManager: NavigationManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.login_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        disposable = viewModel.viewState().observeOn(AndroidSchedulers.mainThread()).subscribe {
            if (it.logging){
                logging.visibility = View.VISIBLE
            } else {
                logging.visibility = View.GONE
            }

            if (it.logged){
                navigationManager.navigateTo(NavigationManager.MAIN , Bundle() , requireActivity())
            }

            if (it.error != null){
                Toast.makeText(requireContext() , it.error.localizedMessage , Toast.LENGTH_LONG).show()
            }
        }

        btn_login.setOnClickListener {
            val email = tv_email.text.toString()
            val pass = tv_pass.text.toString()
            viewModel.login(email, pass)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }

}