package ru.yogago.goyoga.ui.action

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.yogago.goyoga.R
import ru.yogago.goyoga.data.AppConstants
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ActionFragment : Fragment() {

    private lateinit var actionViewModel: ActionViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        actionViewModel = ViewModelProvider(this).get(ActionViewModel::class.java)
        return inflater.inflate(R.layout.fragment_ations, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val image = view.findViewById<ImageView>(R.id.image)
        val progressBarAll = view.findViewById<ProgressBar>(R.id.progressBarAll)
        val progressBarItem = view.findViewById<ProgressBar>(R.id.progressBarItem)
        val title: TextView = view.findViewById(R.id.title)
        val count: TextView = view.findViewById(R.id.count)
        val it = view.findViewById<TextView>(R.id.it)
        val description: TextView = view.findViewById(R.id.description)
        val buttonStart = view.findViewById<ToggleButton>(R.id.buttonStart)

        val anim = AnimationUtils.loadAnimation(context, R.anim.button_anim);

        var countTime = 0

        buttonStart.setOnCheckedChangeListener { compoundButton, b ->
            compoundButton.startAnimation(anim)
            actionViewModel.isPlay = b
            actionViewModel.playAsanas(countTime)
        }

        actionViewModel.progressAll.observe(viewLifecycleOwner, {
            progressBarAll.setProgress(it, true)
        })

        actionViewModel.userData.observe(viewLifecycleOwner, {
            count.text = it.allCount.toString()
            countTime = it.allCount!!
        })

        actionViewModel.asana.observe(viewLifecycleOwner, { asana ->
            GlobalScope.launch(Dispatchers.IO) {
                animateProgressItem(asana.times, progressBarItem)
            }
            title.text = asana.name
            description.text = asana.symmetric
            it.text = asana.id.toString()

            val patch = AppConstants.PHOTO_URL + asana.photo
            Log.d(AppConstants.LOG_TAG, patch)
            val picasso = Picasso.get()
            picasso.setIndicatorsEnabled(false)
            picasso
                .load(patch)
                .resize(320, 214)
                .onlyScaleDown()
                .centerCrop()
                .placeholder(resources.getIdentifier("placeholder", "drawable", "ru.yogago.goyoga"))
                .into(image)

        })

        actionViewModel.loadData()

    }
    private fun animateProgressItem(t: Int, progressBarItem: ProgressBar){
        var time = t*10
        while (time > 1) {
            Thread.sleep(100)
            time -= 1
            val progress: Int = (100 / time)
            progressBarItem.setProgress(progress, true)
        }
    }

}