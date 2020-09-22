package ru.yogago.goyoga.ui.action

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ru.yogago.goyoga.R

class ActionFragment : Fragment() {

    private lateinit var actionViewModel: ActionViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        actionViewModel = ViewModelProvider(this).get(ActionViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_ations, container, false)
        val textView: TextView = root.findViewById(R.id.text_notifications)
        actionViewModel.text.observe(viewLifecycleOwner, {
            textView.text = it
        })
        return root
    }
}