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
import android.widget.ProgressBar
import android.widget.Toast
import android.widget.ToggleButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import ru.yogago.goyoga.R
import ru.yogago.goyoga.data.AppConstants.Companion.LOG_TAG
import ru.yogago.goyoga.data.Asana
import java.util.*
import kotlin.coroutines.CoroutineContext


class ActionFragment : Fragment(), CoroutineScope {

    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job
    private val isRussianLanguage: Boolean = Locale.getDefault().language == "ru"
    private lateinit var viewPager: ViewPager2
    private lateinit var actionViewModel: ActionViewModel
    private var count = 1
    private var ttsEnabled: Boolean = true
    private var myTTS: TextToSpeech? = null
    private val ttsCheckCode = 0
    private lateinit var asanas: List<Asana>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actionViewModel = ViewModelProvider(this).get(ActionViewModel::class.java)
        if (arguments?.getLong("id") != null) {
            actionViewModel.id = requireArguments().getLong("id")
            requireArguments().remove("id")
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

        val buttonStart = view.findViewById<ToggleButton>(R.id.buttonStart)
        val buttonSound = view.findViewById<ToggleButton>(R.id.buttonSound)

        val animForButtonStart = AnimationUtils.loadAnimation(context, R.anim.button_anim)

        val sp = SoundPool.Builder()
            .setMaxStreams(5)
            .build()
        val mSp = sp.load(this.context, R.raw.metronomsound02, 1)

        buttonSound.setOnCheckedChangeListener { compoundButton, b ->
            compoundButton.startAnimation(animForButtonStart)
            if (b) myTTS?.stop()
        }

        buttonStart.setOnCheckedChangeListener { compoundButton, b ->
            compoundButton.startAnimation(animForButtonStart)
            actionViewModel.isPlay = b
            if (b) {
//                animatorForProgressItem.currentPlayTime = animatorItemCurrentTime
//                animatorForProgressItem.start()
            }
            if (!b) {
//                animatorItemCurrentTime = animatorForProgressItem.currentPlayTime
//                animatorForProgressItem.cancel()
                myTTS?.stop()
            }
        }

        // Instantiate a ViewPager2 and a PagerAdapter.
        viewPager = view.findViewById(R.id.pager)
        viewPager.setPageTransformer(ZoomOutPageTransformer())
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                Log.d(LOG_TAG, "ScreenSlidePagerAdapter - onPageSelected position: $position")
                actionViewModel.saveActionState((position + 1), true)
            }
        })

        actionViewModel.asana.observe(viewLifecycleOwner, { asana ->
            sp.play(mSp, 1F, 1F, 1, 0, 1F)
            val title = if (isRussianLanguage) asana.name else asana.eng
            val descriptionText = if (isRussianLanguage) asana.description else asana.description_en

            if (!buttonSound.isChecked)
                myTTS?.speak(descriptionText, TextToSpeech.QUEUE_FLUSH, null, asana.id.toString())

            viewPager.currentItem = (actionViewModel.id - 1).toInt()
        })

        actionViewModel.isFinish.observe(viewLifecycleOwner, {
//            progressBarAll.setProgress(1000, true)
//            buttonStart.isChecked = !it
        })

        actionViewModel.asans.observe(viewLifecycleOwner, {
            asanas = it
        })

        actionViewModel.userData.observe(viewLifecycleOwner, {
            count = it.allCount

            // The pager adapter, which provides the pages to the view pager widget.
            val pagerAdapter = ScreenSlidePagerAdapter()
            viewPager.adapter = pagerAdapter


        })

        actionViewModel.loadData()
    }

    override fun onDestroy() {
        myTTS?.stop()
        myTTS?.shutdown()
        actionViewModel.cancelBackgroundWork()
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

    private inner class ScreenSlidePagerAdapter(fa: FragmentActivity = this.requireActivity()) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int {
            return count
        }

        override fun createFragment(position: Int): Fragment{
            val pos =  (position + 1)
            val pageFragment = PageFragment()
            val args = Bundle()
            args.putInt("id", pos)
            args.putInt("allCount", count)
            args.putBoolean("isRussianLanguage", isRussianLanguage)
            args.putBoolean("isPlay", actionViewModel.isPlay)
            pageFragment.arguments = args


            return pageFragment
        }
    }

}