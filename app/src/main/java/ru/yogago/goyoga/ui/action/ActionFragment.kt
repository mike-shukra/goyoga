package ru.yogago.goyoga.ui.action

import android.animation.ObjectAnimator
import android.content.Intent
import android.media.SoundPool
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import com.yandex.mobile.ads.AdRequest
import com.yandex.mobile.ads.AdSize
import com.yandex.mobile.ads.AdView
import ru.yogago.goyoga.R
import ru.yogago.goyoga.data.AppConstants
import ru.yogago.goyoga.data.AppConstants.Companion.LOG_TAG
import ru.yogago.goyoga.data.BillingState
import ru.yogago.goyoga.service.OkHttpClientFactory
import ru.yogago.goyoga.service.StickyBannerEventListener
import java.util.*


class ActionFragment : Fragment() {

    private var ttsEnabled: Boolean = true
    private lateinit var actionViewModel: ActionViewModel
    private val isRussianLanguage: Boolean = Locale.getDefault().language == "ru"
    private var animatorItemCurrentTime: Long = 0
    private val ttsCheckCode = 0
    private var myTTS: TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actionViewModel = ViewModelProvider(this).get(ActionViewModel::class.java)
        arguments?.let {
            actionViewModel.id = it.getLong("id")
        }

        val checkTTSIntent = Intent()
        checkTTSIntent.action = TextToSpeech.Engine.ACTION_CHECK_TTS_DATA
        startActivityForResult(checkTTSIntent, ttsCheckCode)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_action, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(LOG_TAG, "ActionFragment - onViewCreated")

//        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val image = view.findViewById<ImageView>(R.id.image)
        val progressBarAll = view.findViewById<ProgressBar>(R.id.progressBarAll)
        val progressBarItem = view.findViewById<ProgressBar>(R.id.progressBarItem)
        val title: TextView = view.findViewById(R.id.title)
        val countTextView: TextView = view.findViewById(R.id.count)
        val currentTextView = view.findViewById<TextView>(R.id.current)
        val description: TextView = view.findViewById(R.id.description)
        val buttonStart = view.findViewById<ToggleButton>(R.id.buttonStart)
        val buttonSound = view.findViewById<ToggleButton>(R.id.buttonSound)
        val advertisingBox = view.findViewById<LinearLayout>(R.id.advertising_box)
        val mAdView = view.findViewById<AdView>(R.id.ad_view)
        mAdView.blockId = AppConstants.YANDEX_RTB_ID_ACTION
        mAdView.adSize = AdSize.stickySize(AdSize.FULL_WIDTH)
        val adRequest = AdRequest.Builder().build()
        mAdView.adEventListener = StickyBannerEventListener()

        val animFadeOut = AnimationUtils.loadAnimation(context, R.anim.alpha_out)
        val animForButtonStart = AnimationUtils.loadAnimation(context, R.anim.button_anim)
        val animatorForProgressItem = ObjectAnimator.ofInt(progressBarItem, "progress", 1, 1000)

        val sp = SoundPool.Builder()
            .setMaxStreams(5)
            .build()
        val mSp = sp.load(this.context, R.raw.metronomsound02, 1)

        var allCount = 0

        buttonSound.setOnCheckedChangeListener { compoundButton, b ->
            compoundButton.startAnimation(animForButtonStart)
            if (b) myTTS?.stop()
        }

//        val pager = view.findViewById(R.id.pager) as ViewPager

        buttonStart.setOnCheckedChangeListener { compoundButton, b ->
            compoundButton.startAnimation(animForButtonStart)
            actionViewModel.isPlay = b
            if (b) {
                animatorForProgressItem.currentPlayTime = animatorItemCurrentTime
                animatorForProgressItem.start()
            }
            if (!b) {
                animatorItemCurrentTime = animatorForProgressItem.currentPlayTime
                animatorForProgressItem.cancel()
                myTTS?.stop()
            }
        }

        BillingState.isAds.observe(viewLifecycleOwner, {
            if (it) advertisingBox.visibility = View.VISIBLE
            else advertisingBox.visibility = View.GONE
        })

        actionViewModel.isFinish.observe(viewLifecycleOwner, {
            progressBarAll.setProgress(1000, true)
            buttonStart.isChecked = !it
        })

        actionViewModel.userData.observe(viewLifecycleOwner, {
            allCount = it.allCount
            countTextView.text = it.allCount.toString()
            currentTextView.text = actionViewModel.actionState.currentId.toString()
        })

        actionViewModel.asana.observe(viewLifecycleOwner, { asana ->
            sp.play(mSp, 1F, 1F, 1, 0, 1F)
            mAdView.loadAd(adRequest)
            progressBarAll.setProgress(1000 / allCount * asana.id.toInt(), true)
            animatorForProgressItem.duration = asana.times * 1000.toLong()
            animatorForProgressItem.interpolator = DecelerateInterpolator()
            if (actionViewModel.isPlay) animatorForProgressItem.start()
            title.text = if (isRussianLanguage) asana.name else asana.eng
            val descriptionText = if (isRussianLanguage) asana.description else asana.description_en
            description.text = descriptionText
            if (!buttonSound.isChecked)
                myTTS?.speak(descriptionText, TextToSpeech.QUEUE_FLUSH, null, asana.id.toString())
            currentTextView.text = asana.id.toString()
            val patch = AppConstants.PHOTO_URL + asana.photo
            Log.d(LOG_TAG, patch)


            val picasso = Picasso.Builder(this.requireContext())
                .downloader(OkHttp3Downloader(OkHttpClientFactory.getClient()))
                .build()

            picasso.setIndicatorsEnabled(false)
            picasso
                .load(patch)
                .resize(640, 426)
                .onlyScaleDown()
                .centerCrop()
                .placeholder(resources.getIdentifier("placeholder", "drawable", "ru.yogago.goyoga"))
                .into(image)
            image.startAnimation(animFadeOut)
        })

        actionViewModel.loadData()
    }

    override fun onDestroy() {
        Log.d(LOG_TAG, "ActionFragment - onDestroy this: ${this.hashCode()}")
        actionViewModel.cancelBackgroundWork()
        myTTS?.stop()
        myTTS?.shutdown()
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == ttsCheckCode) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                myTTS = TextToSpeech(this.context) {
                    if (it == TextToSpeech.SUCCESS){
                        Log.d(LOG_TAG, "myTTS.voices: " + myTTS?.voices)
                        myTTS?.language = if (isRussianLanguage) Locale(Locale.getDefault().language) else Locale.US
                        myTTS?.setPitch(1.0f)
                        myTTS?.setSpeechRate(1.0f)
                        ttsEnabled = true
                    }
                    else if (it == TextToSpeech.ERROR) {
                        Toast.makeText(this.context, R.string.tts_error, Toast.LENGTH_LONG).show()
                        ttsEnabled = false
                    }
                }
            } else {
                val installTTSIntent = Intent()
                installTTSIntent.action = TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA
                startActivity(installTTSIntent)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

}