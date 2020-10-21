package ru.yogago.goyoga.ui.profile

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import ru.yogago.goyoga.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}