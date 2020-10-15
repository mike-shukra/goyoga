package ru.yogago.goyoga.ui.profile

import android.content.res.Resources
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import ru.yogago.goyoga.R
import ru.yogago.goyoga.data.*
import ru.yogago.goyoga.data.AppConstants.Companion.LOG_TAG_BILLING

class Adapter(
    private val items: List<BillingItem>,
    private val resources: Resources,
    private val viewLifecycleOwner: LifecycleOwner
): RecyclerView.Adapter<Adapter.ItemViewHolder?>() {

    lateinit var onButtonClick: (BillingItem) -> Unit
    lateinit var onSubscribeClick: (BillingItem) -> Unit

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ItemViewHolder {
        val v = LayoutInflater.from(viewGroup.context).inflate(
            R.layout.card_view_billings,
            viewGroup,
            false
        )
        return ItemViewHolder(v)
    }

    override fun onBindViewHolder(itemViewHolder: ItemViewHolder, position: Int) {
        itemViewHolder.billingItemTitle.text = items[position].title
        itemViewHolder.billingItemType.text = items[position].type
        itemViewHolder.billingItemPrice.text = items[position].price
        itemViewHolder.billingItemSubscriptionPeriod.text = formatPeriod(items[position].subscriptionPeriod, true)
        itemViewHolder.billingItemDescription.text = items[position].description

        when (items[position].sku) {
            JUST_PAY -> {
                BillingState.isJustPay.observe(viewLifecycleOwner, { b ->
                    itemViewHolder.buttonSubscribe.isEnabled = b
                    Log.d(LOG_TAG_BILLING, "Adapter - items[position].sku: ${items[position].sku} - $b")
                })
            }
            REMOVE_ADS, REMOVE_ADS_Y -> {
                BillingState.isAds.observe(viewLifecycleOwner, { b ->
                    itemViewHolder.buttonSubscribe.isEnabled = b
                    Log.d(LOG_TAG_BILLING, "Adapter - items[position].sku: ${items[position].sku} - $b")
                })
            }
        }

//        itemViewHolder.button.setOnClickListener {
//            onButtonClick(items[position])
//        }
        itemViewHolder.buttonSubscribe.setOnClickListener {
            onSubscribeClick(items[position])
        }

    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val billingItemTitle = itemView.findViewById(R.id.billing_item_title) as TextView
        val billingItemType = itemView.findViewById(R.id.billing_item_type) as TextView
        val billingItemPrice = itemView.findViewById(R.id.billing_item_price) as TextView
        val billingItemSubscriptionPeriod = itemView.findViewById(R.id.billing_item_subscriptionPeriod) as TextView
        val billingItemDescription = itemView.findViewById(R.id.billing_item_description) as TextView
        val buttonSubscribe = itemView.findViewById(R.id.buttonSubscribe) as Button
//        val button = itemView.findViewById(R.id.button) as Button
    }

    private fun formatPeriod(period: String, isIncludeSingularNumber: Boolean): String {
        if (period.count() < 3) return ""
        val isSingular = period[1] == '1'
        val unit = when (period[2]) {
            'W' -> if (isSingular) resources.getString(R.string.week) else resources.getString(R.string.weeks)
            'M' -> if (isSingular) resources.getString(R.string.month) else resources.getString(R.string.months)
            'Y' -> if (isSingular) resources.getString(R.string.year) else resources.getString(R.string.years)
            else -> ""
        }
        return if (isSingular && !isIncludeSingularNumber) unit else "${period[1]} $unit"
    }


}