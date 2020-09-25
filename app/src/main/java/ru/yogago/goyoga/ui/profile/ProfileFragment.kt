package ru.yogago.goyoga.ui.profile

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import ru.yogago.goyoga.R
import ru.yogago.goyoga.data.AppConstants
import ru.yogago.goyoga.data.SelectedIndexArray
import ru.yogago.goyoga.ui.login.LoginActivity
import ru.yogago.goyoga.ui.login.LoginViewModel
import ru.yogago.goyoga.ui.login.LoginViewModelFactory

class ProfileFragment : Fragment() {

    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        val application: Application = this.requireActivity().application
        loginViewModel = ViewModelProvider(this, LoginViewModelFactory(application) ).get(LoginViewModel::class.java)
        loginViewModel.setModel()

        val profileLogOutButton: Button = view.findViewById(R.id.profileLogOutButton)
        val profileDeleteButton: Button = view.findViewById(R.id.profileDeleteButton)
        val profileEditButton: Button = view.findViewById(R.id.profileEditButton)
        val profileUserName: TextView = view.findViewById(R.id.profileUserName)
        val profileUserEmail: TextView = view.findViewById(R.id.profileUserEmail)
        val createButton = view.findViewById<Button>(R.id.createButton)
        val levelSpinner = view.findViewById<Spinner>(R.id.levelSpinner)
        val checkBoxKnee = view.findViewById<CheckBox>(R.id.checkBoxKnee)
        val checkBoxNeck = view.findViewById<CheckBox>(R.id.checkBoxNeck)
        val checkBoxLoins = view.findViewById<CheckBox>(R.id.checkBoxLoins)
        val loading = view.findViewById<ProgressBar>(R.id.loading)


        createButton.setOnClickListener {
            profileViewModel.create(
                levelSpinner.selectedItemId.toString(),
                checkBoxKnee.isChecked,
                checkBoxLoins.isChecked,
                checkBoxNeck.isChecked
            )
            findNavController().navigate(R.id.nav_select)
        }

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

            val levels = resources.getStringArray(R.array.levels)
            val selectedIndex = SelectedIndexArray(selectedIndex = levels.indexOf(it.level), arr = levels)
            val adapter = ArrayAdapter(
                this.requireContext(),
                R.layout.spinner,
                selectedIndex.arr as Array<out String>
            )
            levelSpinner.adapter = adapter
            levelSpinner.setSelection(selectedIndex.selectedIndex)

            checkBoxKnee.isChecked = it.dangerknee == 1
            checkBoxNeck.isChecked = it.dangerneck == 1
            checkBoxLoins.isChecked = it.dangerloins == 1

            loading.visibility = View.GONE

        })
        profileViewModel.error.observe(viewLifecycleOwner, {
            Toast.makeText(
                context,
                it,
                Toast.LENGTH_LONG
            ).show()
            if (it == "Не авторизовано") {
                val intent = Intent(this.activity, LoginActivity::class.java)
                startActivity(intent)
                this.activity?.finish()
            }
        })

        profileViewModel.setModel()
        profileViewModel.loadUserData()

    }
}
