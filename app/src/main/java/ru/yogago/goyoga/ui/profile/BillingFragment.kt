package ru.yogago.goyoga.ui.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import androidx.lifecycle.ViewModelProvider
import ru.yogago.goyoga.R
import ru.yogago.goyoga.data.BillingItem
import ru.yogago.goyoga.model.MyBilling
import java.util.ArrayList

class BillingFragment : Fragment() {

    private lateinit var viewModel: BillingViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.billing_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(BillingViewModel::class.java)

        val billingsLv = view.findViewById<ListView>(R.id.billings)

        viewModel.billings.observe(viewLifecycleOwner, {
            val adapter = getAdapter(it)
            billingsLv.adapter = adapter
            billingsLv.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
                viewModel.subscribe(id.toInt())
            }
        })

        activity?.let { fragmentActivity ->
            viewModel.setMyBilling(MyBilling(fragmentActivity))
        }

        viewModel.loadBillings()
    }

    private fun getAdapter(pets: ArrayList<BillingItem>): BillingItemAdapter {
        return BillingItemAdapter(
            this.requireContext(),
            pets
        )
    }

}