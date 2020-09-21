package ru.yogago.goyoga.ui.profile

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import ru.yogago.goyoga.R
import ru.yogago.goyoga.ui.login.LoginActivity
import ru.yogago.goyoga.ui.login.LoginViewModel
import ru.yogago.goyoga.ui.login.LoginViewModelFactory

class ProfileFragment : Fragment() {

    private val LOG_TAG: String = "myLog"
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(LOG_TAG, "ProfileFragment - onCreateView")
        val args = Bundle()
        profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        val application: Application = this.requireActivity().application
        loginViewModel = ViewModelProvider(this, LoginViewModelFactory(application) ).get(LoginViewModel::class.java)
        loginViewModel.setModel()
        val root = inflater.inflate(R.layout.fragment_profile, container, false)

        val profileLogOutButton: Button = root.findViewById(R.id.profileLogOutButton)
        val profileDeleteButton: Button = root.findViewById(R.id.profileDeleteButton)
        val profileEditButton: Button = root.findViewById(R.id.profileEditButton)
        val profileUserName: TextView = root.findViewById(R.id.profileUserName)
        val profileUserEmail: TextView = root.findViewById(R.id.profileUserEmail)

        profileLogOutButton.setOnClickListener {
            loginViewModel.logOut()
            val intent = Intent(this.activity, LoginActivity::class.java)
            startActivity(intent)
            this.activity?.finish()
        }
        profileDeleteButton.setOnClickListener {
            loginViewModel.deleteUser()
            val intent = Intent(this.activity, LoginActivity::class.java)
            startActivity(intent)
            this.activity?.finish()
        }
        profileEditButton.setOnClickListener {
            findNavController().navigate(R.id.nav_editUser)
        }
        profileViewModel.user.observe(viewLifecycleOwner, {
            profileUserName.text = it.first_name
            profileUserEmail.text = it.email

        })
        profileViewModel.error.observe(viewLifecycleOwner, {
            if (it == "Не авторизовано") {
                val intent = Intent(this.activity, LoginActivity::class.java)
                startActivity(intent)
                this.activity?.finish()
            }
        })

        profileViewModel.setModel()
        profileViewModel.loadUserData()

        return root
    }

}
