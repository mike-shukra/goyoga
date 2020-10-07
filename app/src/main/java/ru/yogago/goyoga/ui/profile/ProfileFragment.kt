package ru.yogago.goyoga.ui.profile

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Application
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GestureDetectorCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import ru.yogago.goyoga.BuildConfig
import ru.yogago.goyoga.R
import ru.yogago.goyoga.data.BillingState
import ru.yogago.goyoga.data.SelectedIndexArray
import ru.yogago.goyoga.model.MyBilling
import ru.yogago.goyoga.ui.login.LoginActivity
import ru.yogago.goyoga.ui.login.LoginViewModel
import ru.yogago.goyoga.ui.login.LoginViewModelFactory


class ProfileFragment : Fragment() {

    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        val application: Application = this.requireActivity().application
        loginViewModel = ViewModelProvider(this, LoginViewModelFactory(application)).get(
            LoginViewModel::class.java
        )
        loginViewModel.setModel()

        val profileLogOutButton: Button = view.findViewById(R.id.profileLogOutButton)
        val profileDeleteButton: Button = view.findViewById(R.id.profileDeleteButton)
        val profileEditButton: Button = view.findViewById(R.id.profileEditButton)
        val profileBillingButton: Button = view.findViewById(R.id.profileBillingButton)
        val createButton = view.findViewById<Button>(R.id.createButton)
        val levelSpinner = view.findViewById<Spinner>(R.id.levelSpinner)
        val checkBoxKnee = view.findViewById<CheckBox>(R.id.checkBoxKnee)
        val checkBoxNeck = view.findViewById<CheckBox>(R.id.checkBoxNeck)
        val checkBoxLoins = view.findViewById<CheckBox>(R.id.checkBoxLoins)
        val loading = view.findViewById<ProgressBar>(R.id.loading)
        val mainLayout = view.findViewById<LinearLayout>(R.id.mainLayout)
        val topLayout = view.findViewById<LinearLayout>(R.id.topLayout)
        val profileWrapper: LinearLayout = view.findViewById(R.id.profileWrapper)
        val profileButtonBox: FrameLayout = view.findViewById(R.id.profileButtonBox)
        val buttonMainTransition = view.findViewById<ToggleButton>(R.id.buttonMainTransition)
        val advertisingBox = view.findViewById<LinearLayout>(R.id.advertising_box)

        val flipAnimation = AnimationUtils.loadAnimation(context, R.anim.flip)

        val lSwipeDetector = GestureDetectorCompat(context, MyGestureListener())

        if (BillingState.isAds) advertisingBox.visibility = View.VISIBLE

//        profileBox.setOnTouchListener(View.OnTouchListener())

        profileBillingButton.setOnClickListener {
            findNavController().navigate(R.id.nav_billing)
        }

        buttonMainTransition.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                compoundButton.scaleY = -1.0F
                compoundButton.startAnimation(flipAnimation)
                topLayout.animate()
                    .translationY(0F)
                    .setDuration(300)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            super.onAnimationEnd(animation)
                            topLayout.translationY = 0F
                        }
                    })
            }
            else {
                compoundButton.scaleY = 1.0F
                compoundButton.startAnimation(flipAnimation)
                topLayout.animate()
                    .translationY(-((profileWrapper.height).toFloat()))
                    .setDuration(300)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            super.onAnimationEnd(animation)
                            topLayout.translationY = -((profileWrapper.height).toFloat())
                        }
                    })
            }
        }
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
            val levels = resources.getStringArray(R.array.levels)
            val selectedIndex = SelectedIndexArray(selectedIndex = it.level, arr = levels)
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
            var text = it
            if (it.contains("UnknownHostException")) text = getString(R.string.no_internet)
            if (it.contains("Не авторизовано")) {
                text = getString(R.string.not_authorized)
                val intent = Intent(this.activity, LoginActivity::class.java)
                startActivity(intent)
                this.activity?.finish()
            }
            Toast.makeText(context, text, Toast.LENGTH_LONG).show()
        })

        profileViewModel.setModel()
        profileViewModel.loadUserData()

    }
}
