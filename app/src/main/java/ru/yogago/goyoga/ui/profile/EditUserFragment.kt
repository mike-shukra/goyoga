package ru.yogago.goyoga.ui.profile

import android.os.Bundle
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
import ru.yogago.goyoga.data.UserData
import ru.yogago.goyoga.ui.login.afterTextChanged

class EditUserFragment : Fragment() {

    private lateinit var viewModel: EditUserViewModel
    private var user: UserData = UserData(0)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewModel = ViewModelProvider(this).get(EditUserViewModel::class.java)
        viewModel.setModel().loadUser()

        val root = inflater.inflate(R.layout.edit_user_fragment, container, false)
        val errorView = root.findViewById<TextView>(R.id.error)
        val editUserFragmentPersonName = root.findViewById<EditText>(R.id.editUserFragmentPersonName)
        val editUserFragmentSaveButton: Button = root.findViewById(R.id.editUserFragmentSaveButton)
        val editUserFragmentPassword = root.findViewById<EditText>(R.id.editUserFragmentPassword)
        val editUserFragmentPasswordReplay = root.findViewById<EditText>(R.id.editUserFragmentPasswordReplay)
        val editUserFragmentSavePassButton: Button = root.findViewById(R.id.editUserFragmentSavePassButton)
        val name = editUserFragmentPersonName as TextView

        viewModel.isUpdate.observe(viewLifecycleOwner, {
            if (it) findNavController().navigate(R.id.nav_profile)
        })

        viewModel.error.observe(viewLifecycleOwner, {
            errorView.visibility = View.VISIBLE
            errorView.text = it
        })

        viewModel.user.observe(viewLifecycleOwner, {
            user = it
            name.text = it.first_name
        })

        viewModel.nameFormState.observe(viewLifecycleOwner, Observer {
            val nameState = it ?: return@Observer
            editUserFragmentSaveButton.isEnabled = nameState.isDataValid
            if (nameState.loginError != null) {
                editUserFragmentPersonName.error = getString(nameState.loginError)
            }
        })

        viewModel.passwordFormState.observe(viewLifecycleOwner, Observer {
            val passwordState = it ?: return@Observer

            editUserFragmentSavePassButton.isEnabled = passwordState.isDataValid

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

        editUserFragmentSaveButton.setOnClickListener {
            user.first_name =  editUserFragmentPersonName.text.toString()
            viewModel.updateUserName(user)
        }

        editUserFragmentPersonName.afterTextChanged {
            viewModel.nameDataChanged(
                name = editUserFragmentPersonName.text.toString())
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

        return root
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.cancelBackgroundWork()
    }
}