package ru.yogago.goyoga.ui.profile

import android.app.Activity
import android.content.Context
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
import kotlinx.coroutines.withContext
import ru.yogago.goyoga.LoginActivity
import ru.yogago.goyoga.MainActivity
import ru.yogago.goyoga.R
import ru.yogago.goyoga.data.AppConstants.Companion.LOG_TAG
import ru.yogago.goyoga.data.Settings
import ru.yogago.goyoga.service.ApiFactory
import ru.yogago.goyoga.service.DataBase
import ru.yogago.goyoga.service.Repository
import java.util.*
import kotlin.coroutines.CoroutineContext


class SettingsFragment : Fragment(), CoroutineScope {

    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job
    private val settings: MutableLiveData<Settings> = MutableLiveData()

    private val dao = DataBase.db.getDBDao()
    private val repository = Repository(dao, ApiFactory.API)

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
            val intent = Intent(activity, LoginActivity::class.java)
            Toast.makeText(activity, "Logging Out", Toast.LENGTH_SHORT).show()
            startActivity(intent)
            activity?.finish()

        }
        switchIsSpeakAsanaName.setOnCheckedChangeListener { _, b ->
            launch {
                repository.updateSettingsIsSpeakAsanaName(b)
            }
        }

        restartButton.setOnClickListener {
            val intent = Intent(context, MainActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        settings.observe(viewLifecycleOwner) {
            when (it.language) {
                "ru" -> {
                    checkBoxRussian.isChecked = true
                }
                "en" -> {
                    checkBoxEnglish.isChecked = true
                }
            }
            switchIsSpeakAsanaName.isChecked = it.speakAsanaName
        }

        languageRadioGroup.setOnCheckedChangeListener { radioGroup, i ->
            val radioButton = radioGroup.findViewById<RadioButton>(i)
            var lang: String = radioButton.text as String
            Log.d(LOG_TAG, "SettingsFragment - lang: $lang")
            if (lang == "Английский" || lang == "English") lang = "en"
            if (lang == "Русский" || lang == "Russian") lang = "ru"
            MainActivity.dLocale = Locale(lang)
            launch {
                repository.updateSettingsLanguage(lang)
                val sharedPreferences = context?.getSharedPreferences("settings", Context.MODE_PRIVATE)
                val editor = sharedPreferences!!.edit()
                editor.putString("language", lang)
                editor.apply()
            }
        }

        launch {
            val settingsData = repository.getSettings()
            val sharedPreferences = context?.getSharedPreferences("settings", Context.MODE_PRIVATE)
            val language = sharedPreferences!!.getString("language", Locale.getDefault().language)
            settingsData.language = language!!
            Log.d(LOG_TAG, "SettingsFragment - settingsData: $settingsData")
            settings.postValue(settingsData)
        }
    }

//    private fun updateLocale(context: Context, language: String): Context {
//        val locale = Locale(language)
//        Locale.setDefault(locale)
//
//        val resources = context.resources
//        val config = resources.configuration
//        config.setLocale(locale)
//        config.setLayoutDirection(locale)
//
//        // Обновляем ресурсы с новой конфигурацией
//        return context.createConfigurationContext(config)
//    }
//
//    fun applyLocale(context: Context, language: String) {
//        val updatedContext = updateLocale(context, language)
//
//        if (context is Activity) {
//            (context as Activity).runOnUiThread {
//                context.recreate()
//            }
//        }
//    }
}