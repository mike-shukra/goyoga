package ru.yogago.goyoga.ui.action

import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.ToggleButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.squareup.picasso.Picasso
import ru.yogago.goyoga.R
import ru.yogago.goyoga.data.AppConstants


class ActionFragment : Fragment() {

    private lateinit var actionViewModel: ActionViewModel
    private var isPlay: Boolean = false

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
        lateinit var animatorAll: ObjectAnimator
        lateinit var animatorItem: ObjectAnimator

        buttonStart.setOnCheckedChangeListener { compoundButton, b ->
            compoundButton.startAnimation(anim)
            actionViewModel.isPlay = b
            this.isPlay = b
            actionViewModel.playAsanas()
            animatorAll.start()
        }

        actionViewModel.progressAll.observe(viewLifecycleOwner, {
            progressBarAll.setProgress(it, true)
        })

        actionViewModel.userData.observe(viewLifecycleOwner, {
            count.text = it.allCount.toString()
            animatorAll = ObjectAnimator.ofInt(progressBarAll, "progress", 0, 100)
            animatorAll.duration = it.allTime!!*1000.toLong()

        })

        actionViewModel.asana.observe(viewLifecycleOwner, { asana ->

            animatorItem = ObjectAnimator.ofInt(progressBarItem, "progress", 0, 100)
            animatorItem.duration = asana.times*1000.toLong()
            if (isPlay) animatorItem.start()

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

}