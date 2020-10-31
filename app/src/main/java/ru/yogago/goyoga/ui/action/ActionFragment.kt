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
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import com.yandex.mobile.ads.AdRequest
import com.yandex.mobile.ads.AdSize
import com.yandex.mobile.ads.AdView
import ru.yogago.goyoga.R
import ru.yogago.goyoga.data.*
import ru.yogago.goyoga.data.AppConstants.Companion.LOG_TAG
import ru.yogago.goyoga.service.OkHttpClientFactory
import ru.yogago.goyoga.service.StickyBannerEventListener
import java.util.*


class ActionFragment : Fragment() {
    private var isInstanceState: Boolean = false
    private val isRussianLanguage: Boolean = Locale.getDefault().language == "ru"
    private lateinit var viewPager: ViewPager2
    private lateinit var actionViewModel: ActionViewModel
    private lateinit var actionState: ActionState
    private var count = 1
    private var ttsEnabled: Boolean = true
    private var myTTS: TextToSpeech? = null
    private val ttsCheckCode = 0
    private lateinit var asanaList: List<Asana>
    private var currentAsana: Int = 0
    private var myPageHashMap = hashMapOf<Int, PagerViewHolder>()
    private lateinit var buttonStart: ToggleButton
    private lateinit var buttonSound: ToggleButton
    private var settings: Settings? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actionViewModel = ActionViewModel()
        arguments?.getLong("id")?.let {
            if (it != 0L) {
                currentAsana = (it.toInt() - 1)
                actionViewModel.saveActionState(ActionState(currentId = currentAsana))
                Log.d(LOG_TAG, "ActionFragment - onCreate - arguments?.let - currentAsana: $currentAsana")
            }
            arguments?.remove("id")
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

        val view = inflater.inflate(R.layout.fragment_action, container, false)

        val sp = SoundPool.Builder()
            .setMaxStreams(5)
            .build()
        val mSp = sp.load(this.context, R.raw.metronomsound02, 1)

        viewPager = view.findViewById(R.id.pager)
        viewPager.setPageTransformer(ZoomOutPageTransformer())
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                Log.d(LOG_TAG, "onPageSelected position: $position buttonStart.isChecked: ${buttonStart.isChecked} isInstanceState: $isInstanceState currentAsana: $currentAsana")
                actionViewModel.mCurrentAsana.postValue(position)
                if (!isInstanceState) {
                    actionViewModel.mCurrentAsana.postValue(currentAsana)
                    isInstanceState = true
                }
            }
        })

        actionViewModel.mData.observe(viewLifecycleOwner, { data ->
            actionState = data.actionState!!
            currentAsana = actionState.currentId
            asanaList = data.asanas!!
            settings = data.settings
            count = data.userData?.allCount!!
            val pagerAdapter = ScreenSlidePagerAdapter()
            viewPager.adapter = pagerAdapter

            actionViewModel.mCurrentAsana.observe(viewLifecycleOwner, {
                Log.d(LOG_TAG, "ActionFragment - onViewCreated - currentAsana.observe it: $it")

                actionViewModel.saveActionState(ActionState(currentId = it))
                Log.d(LOG_TAG, "ActionFragment - onViewCreated - saveActionState currentId: $it")

                viewPager.setCurrentItem(it, false)

                actionViewModel.setTime(asanaList[it].times*10)

                if (buttonStart.isChecked) {
                    textToSpeech()
                    isDoAnimationProgressItem(true)
                    actionViewModel.waitAsana()
                } else {
                    myTTS?.stop()
                    actionViewModel.cancelBackgroundWork()
                    isDoAnimationProgressItem(false)
                }

            })

            actionViewModel.go.observe(viewLifecycleOwner, {
                Log.d(LOG_TAG, "ActionFragment - onViewCreated - go currentAsana: $currentAsana i: $it" )
                if  (currentAsana == (asanaList.size - 1)) {
                    Log.d(LOG_TAG, "ActionFragment - onViewCreated - go currentAsana: if  (currentAsana == asanaList.size" )
                    buttonStart.isChecked = false
                }
                else  {
                    actionViewModel.mCurrentAsana.postValue(it)
                    sp.play(mSp, 1F, 1F, 1, 0, 1F)
                }
            })

        })

        actionViewModel.loadData()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonStart = view.findViewById(R.id.buttonStart)
        buttonSound = view.findViewById(R.id.buttonSound)

        val animForButtonStart = AnimationUtils.loadAnimation(context, R.anim.button_anim)


        buttonSound.setOnCheckedChangeListener { compoundButton, b ->
            compoundButton.startAnimation(animForButtonStart)
            if (b) myTTS?.stop()
        }

        buttonStart.setOnCheckedChangeListener { compoundButton, _ ->
            compoundButton.startAnimation(animForButtonStart)
            actionViewModel.mCurrentAsana.postValue(currentAsana)
        }

    }

    private fun isDoAnimationProgressItem(flag: Boolean) {
        Log.d(LOG_TAG, "ActionFragment - isDoAnimationProgressItem - flag: $flag")
        actionViewModel.isHolder.observe(viewLifecycleOwner, {
            myPageHashMap[currentAsana]?.animatorForProgressItem?.let { animator ->
                if (flag) {
                    animator.duration = asanaList[currentAsana].times * 1000.toLong()
                    animator.start()
                } else {
                    animator.cancel()
                }
            }
        })
    }

    private fun textToSpeech() {
        if (!buttonSound.isChecked) {
            myTTS?.stop()
            var name = ""
            var eng = ""
            settings?.let {
                if (it.isSpeakAsanaName) {
                    name = asanaList[currentAsana].name + ". "
                    eng = asanaList[currentAsana].eng + ". "
                }
            }
            val descriptionText =
                if (isRussianLanguage) name + asanaList[currentAsana].description
                else eng + asanaList[currentAsana].description_en
            myTTS?.speak(
                descriptionText,
                TextToSpeech.QUEUE_FLUSH,
                null,
                asanaList[currentAsana].id.toString()
            )
        }
    }

    override fun onDestroy() {
        Log.d(LOG_TAG, "ActionFragment - onDestroy")
        myTTS?.stop()
        myTTS?.shutdown()
        actionViewModel.cancelBackgroundWork()
        myPageHashMap.clear()
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == ttsCheckCode) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                myTTS = TextToSpeech(this.context) {
                    if (it == TextToSpeech.SUCCESS){
                        Log.d(LOG_TAG, "ActionFragment - myTTS.voices: " + myTTS?.voices)
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
            Log.d(LOG_TAG, "ActionFragment - onCreateViewHolder - parent: ${parent.hashCode()} viewType: $viewType")
            return PagerViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.page_action, parent, false))
        }

        override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
            myPageHashMap[position] = holder
            actionViewModel.isHolder.postValue(true)

            Log.d(LOG_TAG, "ActionFragment - onBindViewHolder - position: $position holder: ${holder.hashCode()}")

            holder.title.text = if (isRussianLanguage) asanaList[position].name else asanaList[position].eng
            val descriptionText = if (isRussianLanguage) asanaList[position].description else asanaList[position].description_en
            holder.description.text = descriptionText
            holder.currentTextView.text = asanaList[position].id.toString()
            holder.countTextView.text = count.toString()
            holder.progressBarAll.setProgress(1000 / count * (position + 1), true)
            if ((position + 1) == count) {
                holder.progressBarAll.setProgress(1000, true)
            }

            val adRequest = AdRequest.Builder().build()
            holder.mAdView.adEventListener = StickyBannerEventListener()
            holder.mAdView.loadAd(adRequest)

            BillingState.isAds.observe(viewLifecycleOwner, {
                if (it) holder.advertisingBox.visibility = View.VISIBLE
                else holder.advertisingBox.visibility = View.GONE
            })

            if (asanaList[position].side == "second") holder.repeatIcon.visibility = View.VISIBLE
            else holder.repeatIcon.visibility = View.GONE

            val patch = AppConstants.PHOTO_URL + asanaList[position].photo
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

        val animatorForProgressItem: ObjectAnimator = ObjectAnimator.ofInt(progressBarItem, "progress", 1, 1000)

        init {
            mAdView.blockId = AppConstants.YANDEX_RTB_ID_ACTION
            mAdView.adSize = AdSize.stickySize(AdSize.FULL_WIDTH)
        }
    }

}