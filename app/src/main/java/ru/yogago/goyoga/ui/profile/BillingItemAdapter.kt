package ru.yogago.goyoga.ui.profile

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import ru.yogago.goyoga.R
import ru.yogago.goyoga.data.BillingItem

class BillingItemAdapter(
    private val context: Context,
    private val dataSource: ArrayList<BillingItem>,
    ) : BaseAdapter() {

    private var listener: ((BillingItem) -> Unit)? = null

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val rowView = if (convertView != null) convertView
        else {
            val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            inflater.inflate(R.layout.list_view_billings, parent, false)
        }

        val title = rowView.findViewById(R.id.billing_item_title) as TextView
        val type = rowView.findViewById(R.id.billing_item_type) as TextView
        val price = rowView.findViewById(R.id.billing_item_price) as TextView
        val currencyCode = rowView.findViewById(R.id.billing_item_price_currency_code) as TextView
        val period = rowView.findViewById(R.id.billing_item_price_subscriptionPeriod) as TextView
        val description = rowView.findViewById(R.id.billing_item_description) as TextView

        val billing = getItem(position)
        title.text = billing.title
        type.text = billing.type
        price.text = billing.price
        currencyCode.text = billing.price_currency_code
        period.text = billing.subscriptionPeriod
        description.text = billing.description

        return rowView
    }

    fun setOnTapListener(listener: ((BillingItem) -> Unit)) {
        this.listener = listener
    }

    override fun getItem(position: Int): BillingItem {
        return dataSource[position]
    }

    override fun getItemId(position: Int): Long {
        val billingItem = getItem(position)
        return position.toLong()
    }

    override fun getCount(): Int {
        return dataSource.size
    }
}