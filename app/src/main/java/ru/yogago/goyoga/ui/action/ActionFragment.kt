package ru.yogago.goyoga.ui.action

import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
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
        Log.d(AppConstants.LOG_TAG, "ActionFragment - onViewCreated")

        val image = view.findViewById<ImageView>(R.id.image)
        val progressBarAll = view.findViewById<ProgressBar>(R.id.progressBarAll)
        val progressBarItem = view.findViewById<ProgressBar>(R.id.progressBarItem)
        val title: TextView = view.findViewById(R.id.title)
        val countTextView: TextView = view.findViewById(R.id.count)
        val currentTextView = view.findViewById<TextView>(R.id.current)
        val description: TextView = view.findViewById(R.id.description)
        val buttonStart = view.findViewById<ToggleButton>(R.id.buttonStart)

        val anim = AnimationUtils.loadAnimation(context, R.anim.button_anim);
        val animatorAll = ObjectAnimator.ofInt(progressBarAll, "progress", 1, 1000)
        val animatorItem = ObjectAnimator.ofInt(progressBarItem, "progress", 1, 1000)

        buttonStart.isChecked = actionViewModel.actionState.isPay

        buttonStart.setOnCheckedChangeListener { compoundButton, b ->
            compoundButton.startAnimation(anim)
            actionViewModel.actionState.isPay = b
            if (b) {
                animatorAll.currentPlayTime = actionViewModel.actionState.animatorAllCurrentPlayTime
                animatorAll.start()
                animatorItem.currentPlayTime = actionViewModel.actionState.animatorItemCurrentPlayTime
                animatorItem.start()
            }
            if (!b) {
                actionViewModel.actionState.animatorAllCurrentPlayTime = animatorAll.currentPlayTime
                animatorAll.cancel()
                actionViewModel.actionState.animatorItemCurrentPlayTime = animatorItem.currentPlayTime
                animatorItem.cancel()
            }

        }

        actionViewModel.userData.observe(viewLifecycleOwner, {
            countTextView.text = it.allCount.toString()
            animatorAll.duration = it.allTime!! * 1000.toLong()
            currentTextView.text = actionViewModel.actionState.currentId.toString()
        })

        actionViewModel.asana.observe(viewLifecycleOwner, { asana ->
            animatorItem.duration = asana.times * 1000.toLong()
            animatorItem.interpolator = DecelerateInterpolator()
            if (actionViewModel.actionState.isPay) animatorItem.start()

            title.text = asana.name
            description.text = asana.symmetric
            currentTextView.text = asana.id.toString()

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

    override fun onStop() {
        super.onStop()
        actionViewModel.saveActionState()
    }
}