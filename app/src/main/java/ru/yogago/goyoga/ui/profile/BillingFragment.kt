package ru.yogago.goyoga.ui.profile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.yogago.goyoga.R
import ru.yogago.goyoga.data.AppConstants.LOG_TAG_BILLING
import ru.yogago.goyoga.data.BillingState
import ru.yogago.goyoga.model.MyBilling

class BillingFragment : Fragment() {

    private lateinit var viewModel: BillingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(BillingViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.billing_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val loading = view.findViewById<ProgressBar>(R.id.loading)
        val rvBillings = view.findViewById<RecyclerView>(R.id.rvBillings)

        val glm = GridLayoutManager(context, 1)
        rvBillings.layoutManager = glm

        viewModel.billings.observe(viewLifecycleOwner, { it ->
            loading.visibility = View.GONE
            val adapter = Adapter(it, this.resources, viewLifecycleOwner)

            adapter.onButtonClick = {
                Log.d(LOG_TAG_BILLING, "adapter.onButtonClick: $it")
            }

            adapter.onSubscribeClick = {
                viewModel.subscribe(it.sku)
                Log.d(LOG_TAG_BILLING, "adapter.onSubscribeClick: $it")
            }

            rvBillings.adapter = adapter

        })

        viewModel.setMyBilling(MyBilling(requireActivity()))

        viewModel.loadBillings()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.destroyBilling()
    }

}