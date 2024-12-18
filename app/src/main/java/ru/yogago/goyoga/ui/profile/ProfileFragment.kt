package ru.yogago.goyoga.ui.profile

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
//import com.yandex.mobile.ads.AdRequest
//import com.yandex.mobile.ads.AdSize
//import com.yandex.mobile.ads.AdView
import ru.yogago.goyoga.R
import ru.yogago.goyoga.data.AppConstants
import ru.yogago.goyoga.data.AppConstants.Companion.LOG_TAG
import ru.yogago.goyoga.data.BillingState
import ru.yogago.goyoga.data.SelectedIndexArray
import ru.yogago.goyoga.model.MyBilling
import javax.inject.Inject

//import ru.yogago.goyoga.service.StickyBannerEventListener

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    // Используем Hilt для создания ViewModel
    private val profileViewModel: ProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        profileViewModel.setModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val flipAnimation = AnimationUtils.loadAnimation(context, R.anim.flip)

        val settingsButton: Button = view.findViewById(R.id.settingsButton)
        val profileBillingButton: Button = view.findViewById(R.id.profileBillingButton)
        val profileWebButton: Button = view.findViewById(R.id.profileWebButton)
        val profileInfoButton: Button = view.findViewById(R.id.profileInfoButton)
        val createButton = view.findViewById<Button>(R.id.createButton)
        val levelSpinner = view.findViewById<Spinner>(R.id.levelSpinner)
        val checkBoxKnee = view.findViewById<CheckBox>(R.id.checkBoxKnee)
        val checkBoxNeck = view.findViewById<CheckBox>(R.id.checkBoxNeck)
        val checkBoxInverted = view.findViewById<CheckBox>(R.id.checkBoxInverted)
        val checkBoxLoins = view.findViewById<CheckBox>(R.id.checkBoxLoins)

        val sideBySideSortRadioGroup = view.findViewById<RadioGroup>(R.id.sideBySideSort)
        val radioButtonLong = view.findViewById<RadioButton>(R.id.checkBoxLong)
        val radioButtonShort = view.findViewById<RadioButton>(R.id.checkBoxShort)

        val loading = view.findViewById<ProgressBar>(R.id.loading)
        val topLayout = view.findViewById<LinearLayout>(R.id.topLayout)
        val profileWrapper: LinearLayout = view.findViewById(R.id.profileWrapper)
        val buttonMainTransition = view.findViewById<ToggleButton>(R.id.buttonMainTransition)
        val advertisingBox = view.findViewById<LinearLayout>(R.id.advertising_box)

        val seekBarProportionallyValue = view.findViewById<TextView>(R.id.seekBarProportionallyValue)
        val seekBarAddTimeValue = view.findViewById<TextView>(R.id.seekBarAddTimeValue)
        val seekBarProportionally = view.findViewById<SeekBar>(R.id.seekBarProportionally)
        val seekBarAddTime = view.findViewById<SeekBar>(R.id.seekBarAddTime)


        sideBySideSortRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            val isCheckedLong = checkedId == radioButtonLong.id
            val isCheckedShort = checkedId == radioButtonShort.id

//            profileViewModel.updateSettingsHowToSort(isCheckedShort)
        }

        seekBarProportionally.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                val value = (p1 / 500F + 1)
                val formattedDouble = String.format("%.2f", value)
                seekBarProportionallyValue.text = formattedDouble
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}

            override fun onStopTrackingTouch(p0: SeekBar?) {
                val value = (seekBarProportionally.progress / 500F + 1)
                val formattedDouble = String.format("%.2f", value)
                seekBarProportionallyValue.text = formattedDouble
//                profileViewModel.updateSettingsProportionately(value)
            }

        })

        seekBarAddTime.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                seekBarAddTimeValue.text = p1.toString()
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}

            override fun onStopTrackingTouch(p0: SeekBar?) {
                val value = (seekBarAddTime.progress)
                seekBarAddTimeValue.text = value.toString()
//                profileViewModel.updateSettingsAddTime(value)
            }

        })

        profileViewModel.proportionately.observe(viewLifecycleOwner) {
            val f = (500 * (it - 1))
            Log.d(LOG_TAG, "ProfileFragment seekBarProportionally progress: $f")
            seekBarProportionally.progress = f.toInt()
        }
        profileViewModel.addTime.observe(viewLifecycleOwner) {
            seekBarAddTime.progress = it
        }


//        val mAdView = view.findViewById<AdView>(R.id.ad_view)
//        mAdView.blockId = YANDEX_RTB_ID_PROFILE
//        mAdView.adSize = AdSize.stickySize(AdSize.FULL_WIDTH)
//        val adRequest = AdRequest.Builder().build()
//        mAdView.adEventListener = StickyBannerEventListener()
//        mAdView.loadAd(adRequest)

        profileWebButton.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://yoga-go.ru/KnowlegeBase/yogaTechniques"))
            startActivity(browserIntent)
        }

        settingsButton.setOnClickListener {
            findNavController().navigate(R.id.nav_settings)
        }

        profileBillingButton.setOnClickListener {
            findNavController().navigate(R.id.nav_billing)
        }

        profileInfoButton.setOnClickListener {
            findNavController().navigate(R.id.nav_info)
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
            Log.d(LOG_TAG, "ProfileFragment createButton OnClickListener it: $it")
            profileViewModel.create(
                levelSpinner.selectedItemId,
                seekBarProportionallyValue.text.toString().toFloat(),
                seekBarAddTimeValue.text.toString().toInt(),
                checkBoxKnee.isChecked,
                checkBoxLoins.isChecked,
                checkBoxNeck.isChecked,
                checkBoxInverted.isChecked,
                radioButtonShort.isChecked
            )
        }

        lifecycleScope.launchWhenStarted {
            profileViewModel.navigationFlow.collect {
                findNavController().navigate(R.id.nav_select)
            }
        }

        BillingState.isAds.observe(viewLifecycleOwner) {
            if (it) advertisingBox.visibility = View.VISIBLE
            else advertisingBox.visibility = View.GONE
        }

        profileViewModel.userData.observe(viewLifecycleOwner) {
            val levels = resources.getStringArray(R.array.levels)
            val selectedIndex = SelectedIndexArray(selectedIndex = it.level, arr = levels)
            val adapter = ArrayAdapter(
                this.requireContext(),
                R.layout.spinner,
                selectedIndex.arr as Array<out String>
            )
            levelSpinner.adapter = adapter
            levelSpinner.setSelection(selectedIndex.selectedIndex)

            checkBoxKnee.isChecked = it.dangerknee
            checkBoxNeck.isChecked = it.dangerneck
            checkBoxLoins.isChecked = it.dangerloins
            checkBoxInverted.isChecked = it.inverted

            when (it.sideBySideSort) {
                true -> {
                    radioButtonShort.isChecked = true
                }

                false -> {
                    radioButtonLong.isChecked = true
                }
            }

            loading.visibility = View.GONE

        }
        profileViewModel.error.observe(viewLifecycleOwner) {
            Log.d(LOG_TAG, "profileViewModel.error.observe: $it")
            var text = ""
            if (it != null)
                text = it

            if (text.contains("UnknownHostException")) text = getString(R.string.no_internet)
            if (text.contains("Не авторизовано")) {
                text = getString(R.string.no_auth)
                profileViewModel.deleteUserData()
            }
            if (text.contains("ksd2564kLJdisda82fd3498")) {
                text = getString(R.string.version_is_out_of_date)
                profileViewModel.deleteUserData()
            }

            Toast.makeText(context, text, Toast.LENGTH_LONG).show()
        }

        profileViewModel.handleBilling()
        profileViewModel.loadUserData()

    }

    override fun onDestroy() {
        super.onDestroy()
        profileViewModel.destroyBilling()
    }

}
