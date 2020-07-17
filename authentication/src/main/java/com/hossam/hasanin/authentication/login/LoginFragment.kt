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
import com.hossam.hasanin.watchittogeter.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.login_fragment.*

@AndroidEntryPoint
class LoginFragment : Fragment() {

    lateinit var disposable: Disposable
    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.login_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        disposable = viewModel.viewState().subscribe {
            if (it.logging){
                logging.visibility = View.VISIBLE
            } else {
                logging.visibility = View.GONE
            }

            if (it.logged){
                activity?.startActivity(Intent(activity , MainActivity::class.java))
                activity?.finish()
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