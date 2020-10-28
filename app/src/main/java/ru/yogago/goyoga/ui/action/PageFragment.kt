package ru.yogago.goyoga.ui.action

import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import com.yandex.mobile.ads.AdRequest
import com.yandex.mobile.ads.AdSize
import com.yandex.mobile.ads.AdView
import kotlinx.android.synthetic.main.page_action.*
import ru.yogago.goyoga.R
import ru.yogago.goyoga.data.AppConstants
import ru.yogago.goyoga.data.AppConstants.Companion.LOG_TAG
import ru.yogago.goyoga.data.Asana
import ru.yogago.goyoga.data.BillingState
import ru.yogago.goyoga.service.OkHttpClientFactory
import ru.yogago.goyoga.service.StickyBannerEventListener


class PageFragment : Fragment() {

    private lateinit var pageViewModel: PageViewModel
    private var isPlay: Boolean = false
    private var allCount: Int = 0
    private var animatorItemCurrentTime: Long = 0
    private var pageId = 1
    private var isRussianLanguage: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageViewModel = ViewModelProvider(this).get(PageViewModel::class.java)

        arguments?.let {
            pageId = it.getInt("id")
            isRussianLanguage = it.getBoolean("isRussianLanguage")
            allCount = it.getInt("allCount")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.page_action, container, false)
    }

//    override fun onResume() {
//        super.onResume()
//        val text = title.text.toString()
//        Toast.makeText(context, text, Toast.LENGTH_LONG).show()
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(LOG_TAG, "PageFragment - onViewCreated")

        val image = view.findViewById<ImageView>(R.id.image)
        val repeatIcon = view.findViewById<ImageView>(R.id.repeatIcon)
        val progressBarAll = view.findViewById<ProgressBar>(R.id.progressBarAll)
        val progressBarItem = view.findViewById<ProgressBar>(R.id.progressBarItem)
        val title: TextView = view.findViewById(R.id.title)
        val countTextView: TextView = view.findViewById(R.id.count)
        val currentTextView = view.findViewById<TextView>(R.id.current)
        val description: TextView = view.findViewById(R.id.description)
        val advertisingBox = view.findViewById<LinearLayout>(R.id.advertising_box)
        val mAdView = view.findViewById<AdView>(R.id.ad_view)
        mAdView.blockId = AppConstants.YANDEX_RTB_ID_ACTION
        mAdView.adSize = AdSize.stickySize(AdSize.FULL_WIDTH)
        val adRequest = AdRequest.Builder().build()
        mAdView.adEventListener = StickyBannerEventListener()

        val animFadeOut = AnimationUtils.loadAnimation(context, R.anim.alpha_out)
        val animatorForProgressItem = ObjectAnimator.ofInt(progressBarItem, "progress", 1, 1000)

        countTextView.text = allCount.toString()
        currentTextView.text = pageId.toString()

        BillingState.isAds.observe(viewLifecycleOwner, {
            if (it) advertisingBox.visibility = View.VISIBLE
            else advertisingBox.visibility = View.GONE
        })

        pageViewModel.isPlay.observe(viewLifecycleOwner, {
            isPlay = it
        })

        pageViewModel.asana.observe(viewLifecycleOwner, { asana ->
            progressBarAll.setProgress(1000 / allCount * pageId, true)
            animatorForProgressItem.duration = asana.times * 1000.toLong()
            if (isPlay) animatorForProgressItem.start()

            if (asana.side == "second") repeatIcon.visibility = View.VISIBLE
            else repeatIcon.visibility = View.GONE
            mAdView.loadAd(adRequest)

            title.text = if (isRussianLanguage) asana.name else asana.eng
            val descriptionText = if (isRussianLanguage) asana.description else asana.description_en
            description.text = descriptionText

            currentTextView.text = asana.id.toString()
            val patch = AppConstants.PHOTO_URL + asana.photo
            Log.d(LOG_TAG, patch)

            val picasso = Picasso.Builder(this.requireContext())
                .downloader(OkHttp3Downloader(OkHttpClientFactory().getClient()))
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

        pageViewModel.loadData(pageId)

    }



}