package ru.yogago.goyoga.ui.action

import android.animation.ObjectAnimator
import android.media.SoundPool
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
import ru.yogago.goyoga.data.AppConstants.LOG_TAG


class ActionFragment : Fragment() {

    private lateinit var actionViewModel: ActionViewModel
    private val isRussianLanguage: Boolean = java.util.Locale.getDefault().language == "ru"

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
        Log.d(LOG_TAG, "ActionFragment - onViewCreated")

        val image = view.findViewById<ImageView>(R.id.image)
        val progressBarAll = view.findViewById<ProgressBar>(R.id.progressBarAll)
        val progressBarItem = view.findViewById<ProgressBar>(R.id.progressBarItem)
        val title: TextView = view.findViewById(R.id.title)
        val countTextView: TextView = view.findViewById(R.id.count)
        val currentTextView = view.findViewById<TextView>(R.id.current)
        val description: TextView = view.findViewById(R.id.description)
        val buttonStart = view.findViewById<ToggleButton>(R.id.buttonStart)

        val animFadeOut = AnimationUtils.loadAnimation(context, R.anim.alpha_out);
        val animFadeIn = AnimationUtils.loadAnimation(context, R.anim.alpha_in);
        val animForButtonStart = AnimationUtils.loadAnimation(context, R.anim.button_anim);
        val animatorForProgressAll = ObjectAnimator.ofInt(progressBarAll, "progress", 1, 1000)
        val animatorForProgressItem = ObjectAnimator.ofInt(progressBarItem, "progress", 1, 1000)

        val sp = SoundPool.Builder()
            .setMaxStreams(5)
            .build()
        val mSp = sp.load(this.context, R.raw.metronomsound02, 1)

        buttonStart.isChecked = actionViewModel.actionState.isPay

        buttonStart.setOnCheckedChangeListener { compoundButton, b ->
            compoundButton.startAnimation(animForButtonStart)
            actionViewModel.actionState.isPay = b
            if (b) {
                animatorForProgressAll.currentPlayTime = actionViewModel.actionState.animatorAllCurrentPlayTime
                animatorForProgressAll.start()
                animatorForProgressItem.currentPlayTime = actionViewModel.actionState.animatorItemCurrentPlayTime
                animatorForProgressItem.start()
            }
            if (!b) {
                actionViewModel.actionState.animatorAllCurrentPlayTime = animatorForProgressAll.currentPlayTime
                animatorForProgressAll.cancel()
                actionViewModel.actionState.animatorItemCurrentPlayTime = animatorForProgressItem.currentPlayTime
                animatorForProgressItem.cancel()
            }

        }

        actionViewModel.userData.observe(viewLifecycleOwner, {
            countTextView.text = it.allCount.toString()
            animatorForProgressAll.duration = it.allTime!! * 1000.toLong()
            currentTextView.text = actionViewModel.actionState.currentId.toString()
        })

        actionViewModel.asana.observe(viewLifecycleOwner, { asana ->
            sp.play(mSp, 1F, 1F, 1, 0, 1F)

//            image.startAnimation(animFadeOut)
            animatorForProgressItem.duration = asana.times * 1000.toLong()
            animatorForProgressItem.interpolator = DecelerateInterpolator()
            if (actionViewModel.actionState.isPay) animatorForProgressItem.start()

            title.text = if (isRussianLanguage) asana.name else asana.eng
            description.text = if (isRussianLanguage) asana.description else asana.description_en
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
            image.startAnimation(animFadeOut)
        })

        actionViewModel.loadData()

    }

    override fun onStop() {
        super.onStop()
        actionViewModel.cancelBackgroundWork()
        actionViewModel.saveActionState()
    }
}