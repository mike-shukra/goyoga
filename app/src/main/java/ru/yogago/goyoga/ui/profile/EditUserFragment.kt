package ru.yogago.goyoga.ui.profile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import ru.yogago.goyoga.R
import ru.yogago.goyoga.data.AppConstants.LOG_TAG
import ru.yogago.goyoga.data.UserData
import ru.yogago.goyoga.ui.login.afterTextChanged

class EditUserFragment : Fragment() {

    private lateinit var viewModel: EditUserViewModel
    private lateinit var user: UserData

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewModel = ViewModelProvider(this).get(EditUserViewModel::class.java)
        viewModel.setModel().loadUser()

        val root = inflater.inflate(R.layout.edit_user_fragment, container, false)
        val editUserFragmentPersonName = root.findViewById(R.id.editUserFragmentPersonName) as TextView
        val editUserFragmentSaveButton: Button = root.findViewById(R.id.editUserFragmentSaveButton)
        val editUserFragmentPassword = root.findViewById(R.id.editUserFragmentPassword) as EditText
        val editUserFragmentPasswordReplay = root.findViewById(R.id.editUserFragmentPasswordReplay) as EditText
        val editUserFragmentSavePassButton: Button = root.findViewById(R.id.editUserFragmentSavePassButton)

        viewModel.isUpdate.observe(viewLifecycleOwner, {
            if (it) findNavController().navigate(R.id.nav_profile)
        })

        viewModel.user.observe(viewLifecycleOwner, {
            user = it
            editUserFragmentPersonName.text = it.first_name
        })

        viewModel.passwordFormState.observe(viewLifecycleOwner, Observer {
            val passwordState = it ?: return@Observer

            editUserFragmentSavePassButton.isEnabled = passwordState.isDataValid
            Log.d(LOG_TAG, "registerSavePassButton.isEnabled: " + passwordState.isDataValid)

            if (passwordState.passwordError != null) {
                editUserFragmentPassword.error = getString(passwordState.passwordError)
            }
            if (passwordState.passwordReplayError != null) {
                editUserFragmentPasswordReplay.error = getString(passwordState.passwordReplayError)
            }
        })

        editUserFragmentSavePassButton.setOnClickListener {
            val password = editUserFragmentPassword.text.toString()
            viewModel.updatePassword(password)
        }

        editUserFragmentPasswordReplay.afterTextChanged {
            viewModel.passwordDataChanged(
                password = editUserFragmentPassword.text.toString(),
                passwordReplay = editUserFragmentPasswordReplay.text.toString())
        }

        editUserFragmentPassword.afterTextChanged {
            viewModel.passwordDataChanged(
                password = editUserFragmentPassword.text.toString(),
                passwordReplay = editUserFragmentPasswordReplay.text.toString())
        }

        editUserFragmentSaveButton.setOnClickListener {
            user.first_name =  editUserFragmentPersonName.text.toString()
            viewModel.updateUserInfo(user)
        }

        return root
    }
}