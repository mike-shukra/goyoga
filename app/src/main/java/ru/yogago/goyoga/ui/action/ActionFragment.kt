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
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import com.yandex.mobile.ads.AdRequest
import com.yandex.mobile.ads.AdSize
import com.yandex.mobile.ads.AdView
import ru.yogago.goyoga.R
import ru.yogago.goyoga.data.AppConstants
import ru.yogago.goyoga.data.AppConstants.Companion.LOG_TAG
import ru.yogago.goyoga.data.Asana
import ru.yogago.goyoga.data.BillingState
import ru.yogago.goyoga.service.OkHttpClientFactory
import ru.yogago.goyoga.service.StickyBannerEventListener
import java.util.*


class ActionFragment : Fragment() {

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
        arguments?.let {
            actionViewModel.saveActionState(it.getLong("id").toInt(), false)
            it.remove("id")
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
//                actionViewModel.saveActionState((position + 1), true)

                val myFragment = childFragmentManager.findFragmentByTag("f$position")
                myFragment?.view?.findViewById<TextView>(R.id.title)?.text = "position: $position"

            }
        })

        actionViewModel.asana.observe(viewLifecycleOwner, { asana ->
            sp.play(mSp, 1F, 1F, 1, 0, 1F)
            val title = if (isRussianLanguage) asana.name else asana.eng
            val descriptionText = if (isRussianLanguage) asana.description else asana.description_en

            if (!buttonSound.isChecked)
                myTTS?.speak(descriptionText, TextToSpeech.QUEUE_FLUSH, null, asana.id.toString())

            viewPager.setCurrentItem((asana.id - 1).toInt(), false)

        })

        actionViewModel.isFinish.observe(viewLifecycleOwner, {
//            progressBarAll.setProgress(1000, true)
//            buttonStart.isChecked = !it
        })

        actionViewModel.asanas.observe(viewLifecycleOwner, {
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

    private inner class ScreenSlidePagerAdapter : RecyclerView.Adapter<PagerViewHolder>() {

        override fun getItemCount(): Int {
            return count
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerViewHolder {
            return PagerViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.page_action, parent, false))
        }

        override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
            holder.title.text = if (isRussianLanguage) asanas[position].name else asanas[position].eng
            val descriptionText = if (isRussianLanguage) asanas[position].description else asanas[position].description_en
            holder.description.text = descriptionText
            holder.currentTextView.text = asanas[position].id.toString()
            holder.countTextView.text = count.toString()
            holder.progressBarAll.setProgress(1000 / count * (position + 1), true)

            val animatorForProgressItem: ObjectAnimator = ObjectAnimator.ofInt(holder.progressBarItem, "progress", 1, 1000)
            animatorForProgressItem.duration = asanas[position].times * 1000.toLong()
            if (actionViewModel.isPlay) {
                animatorForProgressItem.start()
            }

            val adRequest = AdRequest.Builder().build()
            holder.mAdView.adEventListener = StickyBannerEventListener()
            holder.mAdView.loadAd(adRequest)


            BillingState.isAds.observe(viewLifecycleOwner, {
                if (it) holder.advertisingBox.visibility = View.VISIBLE
                else holder.advertisingBox.visibility = View.GONE
            })

            if (asanas[position].side == "second") holder.repeatIcon.visibility = View.VISIBLE
            else holder.repeatIcon.visibility = View.GONE

            val patch = AppConstants.PHOTO_URL + asanas[position].photo
            Log.d(LOG_TAG, patch)

            val picasso = Picasso.Builder(requireContext())
                .downloader(OkHttp3Downloader(OkHttpClientFactory().getClient()))
                .build()

            picasso.setIndicatorsEnabled(false)
            picasso
                .load(patch)
                .resize(640, 426)
                .onlyScaleDown()
                .centerCrop()
                .placeholder(resources.getIdentifier("placeholder", "drawable", "ru.yogago.goyoga"))
                .into(holder.image)

            holder.image.startAnimation(holder.animFadeOut)

        }
    }

    private inner class PagerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val image: ImageView = itemView.findViewById(R.id.image)
        val repeatIcon: ImageView = itemView.findViewById(R.id.repeatIcon)
        val progressBarAll: ProgressBar = itemView.findViewById(R.id.progressBarAll)
        val progressBarItem: ProgressBar = itemView.findViewById(R.id.progressBarItem)
        val title: TextView = itemView.findViewById(R.id.title)
        val countTextView: TextView = itemView.findViewById(R.id.count)
        val currentTextView: TextView = itemView.findViewById(R.id.current)
        val description: TextView = itemView.findViewById(R.id.description)
        val advertisingBox: LinearLayout = itemView.findViewById(R.id.advertising_box)
        val mAdView: AdView = itemView.findViewById(R.id.ad_view)
        val animFadeOut: Animation = AnimationUtils.loadAnimation(context, R.anim.alpha_out)

        init {
            mAdView.blockId = AppConstants.YANDEX_RTB_ID_ACTION
            mAdView.adSize = AdSize.stickySize(AdSize.FULL_WIDTH)
        }
    }

}