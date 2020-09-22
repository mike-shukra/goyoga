package ru.yogago.goyoga.ui.select

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ru.yogago.goyoga.R
import ru.yogago.goyoga.ui.login.LoginActivity

class SelectFragment : Fragment() {

    private lateinit var selectViewModel: SelectViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        selectViewModel = ViewModelProvider(this).get(SelectViewModel::class.java)
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val textView: TextView = view.findViewById(R.id.text_home)
        selectViewModel.text.observe(viewLifecycleOwner, {
            textView.text = it
        })

        selectViewModel.asanas.observe(viewLifecycleOwner, {
        })

        selectViewModel.error.observe(viewLifecycleOwner, {
            if (it == "Не авторизовано") {
                val intent = Intent(this.context, LoginActivity::class.java)
                startActivity(intent)
            }
        })

        selectViewModel.setModel()
        selectViewModel.loadAsanas()


    }
}