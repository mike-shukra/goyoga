package ru.yogago.goyoga.ui.select

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.OkHttpClient
//import com.yandex.mobile.ads.AdRequest
//import com.yandex.mobile.ads.AdSize
//import com.yandex.mobile.ads.AdView
import ru.yogago.goyoga.R
import ru.yogago.goyoga.data.AppConstants.Companion.YANDEX_RTB_ID_SELECT_320X100
import ru.yogago.goyoga.data.AppConstants.Companion.YANDEX_RTB_ID_SELECT_VERTICAL
import ru.yogago.goyoga.data.BillingState
import javax.inject.Inject

//import ru.yogago.goyoga.service.StickyBannerEventListener

@AndroidEntryPoint
class SelectFragment : Fragment() {

    @Inject
    lateinit var okHttpClient: OkHttpClient

    private val selectViewModel: SelectViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_select, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val time = view.findViewById<TextView>(R.id.time)
        val count = view.findViewById<TextView>(R.id.count)
        val loading = view.findViewById<ProgressBar>(R.id.loading)
        val rvAsanas: RecyclerView = view.findViewById(R.id.rvAsanas)
        val glm = GridLayoutManager(context, getScreenColumn())
        rvAsanas.layoutManager = glm
        val advertisingBox = view.findViewById<FrameLayout>(R.id.advertising_box)
        val picasso = Picasso.Builder(this.requireContext())
            .downloader(OkHttp3Downloader(okHttpClient))
            .build()

//        val mAdView = view.findViewById<AdView>(R.id.ad_view)
//        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            mAdView.blockId = YANDEX_RTB_ID_SELECT_VERTICAL
//            mAdView.adSize = AdSize.BANNER_240x400
//        }
//        else {
//            mAdView.blockId = YANDEX_RTB_ID_SELECT_320X100
//            mAdView.adSize = AdSize.stickySize(AdSize.FULL_WIDTH)
//        }
//
//        val adRequest = AdRequest.Builder().build()
//        mAdView.adEventListener = StickyBannerEventListener()
//        mAdView.loadAd(adRequest)

        BillingState.isAds.observe(viewLifecycleOwner) {
            if (it) advertisingBox.visibility = View.VISIBLE
            else advertisingBox.visibility = View.GONE
        }

        selectViewModel.userData.observe(viewLifecycleOwner) {
            time.text = (it.allTime / 60).toString()
            count.text = it.allCount.toString()
        }

        selectViewModel.asanas.observe(viewLifecycleOwner) {
            loading.visibility = View.GONE

            val adapter = Adapter(it, this.resources, picasso)
            rvAsanas.adapter = adapter

            adapter.onItemClick = { asana ->
                if (asana.side == "second")
                    asana.id--
                val args = Bundle()
                args.putLong("id", asana.id)
                findNavController().navigate(R.id.nav_action, args)
            }
        }

        selectViewModel.error.observe(viewLifecycleOwner) {
            var text = it
            if (it.contains("UnknownHostException"))
                text = getString(R.string.no_internet)
            if (it.contains("Не авторизовано"))
                text = getString(R.string.not_created_yet)
            if (it.contains("ksd2564kLJdisda82fd3498"))
                text = getString(R.string.version_is_out_of_date)

            Toast.makeText(context, text, Toast.LENGTH_LONG).show()
        }

        selectViewModel.setModel()
        selectViewModel.loadAsanas()
    }

    private fun getScreenColumn(): Int {
        return if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) 3 else 2
    }

}