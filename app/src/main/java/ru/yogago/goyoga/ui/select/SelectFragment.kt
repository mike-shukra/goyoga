package ru.yogago.goyoga.ui.select

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
        return inflater.inflate(R.layout.fragment_select, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val time = view.findViewById<TextView>(R.id.time)
        val count = view.findViewById<TextView>(R.id.count)
        val loading = view.findViewById<ProgressBar>(R.id.loading)
        val rvAsanas: RecyclerView = view.findViewById(R.id.rvAsanas)
        val glm = GridLayoutManager(context, 2)
        rvAsanas.layoutManager = glm

        selectViewModel.userData.observe(viewLifecycleOwner, {
            time.text = (it.allTime / 60).toString()
            count.text = it.allCount.toString()
        })

        selectViewModel.asanas.observe(viewLifecycleOwner, {
            loading.visibility = View.GONE
            val adapter = Adapter(it, this.resources)
            rvAsanas.adapter = adapter
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

    override fun onDestroy() {
        super.onDestroy()
        selectViewModel.cancelBackgroundWork()
    }

}