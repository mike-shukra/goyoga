package ru.yogago.goyoga.ui.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import ru.yogago.goyoga.LoginActivity
import ru.yogago.goyoga.MainActivity
import ru.yogago.goyoga.R
import ru.yogago.goyoga.data.AppConstants.Companion.LOG_TAG
import ru.yogago.goyoga.data.Settings
import ru.yogago.goyoga.service.DataBase
import java.util.*
import kotlin.coroutines.CoroutineContext


class SettingsFragment : Fragment(), CoroutineScope {

    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job
    private val settings: MutableLiveData<Settings> = MutableLiveData()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.settings_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val languageRadioGroup = view.findViewById<RadioGroup>(R.id.language)
        val checkBoxEnglish = view.findViewById<RadioButton>(R.id.checkBoxEnglish)
        val checkBoxRussian = view.findViewById<RadioButton>(R.id.checkBoxRussian)
        val restartButton = view.findViewById<Button>(R.id.restartButton)
        val logOutButton = view.findViewById<Button>(R.id.btnLogout)
        val switchIsSpeakAsanaName = view.findViewById<SwitchCompat>(R.id.switchIsSpeakAsanaName)


        logOutButton.setOnClickListener {
            Firebase.auth.signOut()

//            val oneTapClient = Identity.getSignInClient(this)
//            oneTapClient.signOut()

            val intent = Intent(activity, LoginActivity::class.java)
            Toast.makeText(activity, "Logging Out", Toast.LENGTH_SHORT).show()
            startActivity(intent)
            activity?.finish()

        }
        switchIsSpeakAsanaName.setOnCheckedChangeListener { _, b ->
            launch {
                DataBase.db.getDBDao().updateSettingsIsSpeakAsanaName(b)
            }
        }

        restartButton.setOnClickListener {
            launch {
                val settings = DataBase.db.getDBDao().getSettings()
                val change = when (settings.language) {
                    "Russian" -> {
                        "ru"
                    }
                    "English" -> {
                        "en"
                    }
                    else -> {
                        ""
                    }
                }
                MainActivity.dLocale = Locale(change)
                val intent = Intent(context, MainActivity::class.java)
                startActivity(intent)
                activity?.finish()
            }

        }

        settings.observe(viewLifecycleOwner) {
            when (it.language) {
                "Russian" -> {
                    checkBoxRussian.isChecked = true
                }

                "English" -> {
                    checkBoxEnglish.isChecked = true
                }
            }
            switchIsSpeakAsanaName.isChecked = it.speakAsanaName
        }

        languageRadioGroup.setOnCheckedChangeListener { radioGroup, i ->
            val radioButton = radioGroup.findViewById<RadioButton>(i)
            var lang: String = radioButton.text as String
            Log.d(LOG_TAG, "SettingsFragment - lang: $lang")
            if (lang == "Английский") lang = "English"
            if (lang == "Русский") lang = "Russian"
            launch {
                DataBase.db.getDBDao().updateSettingsLanguage(lang)
            }
        }

        launch {
            val settingsData = DataBase.db.getDBDao().getSettings()
            settingsData?.let {
                settings.postValue(it)
            }
        }
    }
}