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
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import ru.yogago.goyoga.MainActivity
import ru.yogago.goyoga.R
import ru.yogago.goyoga.data.AppConstants.Companion.LOG_TAG
import ru.yogago.goyoga.service.DataBase
import java.util.*
import kotlin.coroutines.CoroutineContext


class SettingsFragment : Fragment(), CoroutineScope {

    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job
    private val language: MutableLiveData<String> = MutableLiveData()


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

        restartButton.setOnClickListener {
            launch {
                val settings = DataBase.db.getDBDao().getSettings()
                val change = when (settings?.language) {
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

        launch {
            val settings = DataBase.db.getDBDao().getSettings()
            settings?.language.let {
                language.postValue(it)
            }
        }

        language.observe(viewLifecycleOwner, {
            when (it) {
                "Russian" -> {
                    checkBoxRussian.isChecked = true
                }
                "English" -> {
                    checkBoxEnglish.isChecked = true
                }
            }

        })

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

    }

}