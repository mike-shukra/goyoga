package ru.yogago.goyoga.ui.profile

import android.content.res.Resources
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import ru.yogago.goyoga.R
import ru.yogago.goyoga.data.AppConstants.LOG_TAG
import ru.yogago.goyoga.data.AppConstants.PHOTO_URL
import ru.yogago.goyoga.data.Asana
import ru.yogago.goyoga.data.BillingItem
import java.util.*

class Adapter(private val items: List<BillingItem>, private val resources: Resources): RecyclerView.Adapter<Adapter.ItemViewHolder?>() {

    lateinit var onItemClick: ((BillingItem) -> Unit)
    lateinit var onSubscribeClick: ((BillingItem) -> Unit)

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
        itemViewHolder.billingItemSubscriptionPeriod.text = items[position].subscriptionPeriod
        itemViewHolder.billingItemDescription.text = items[position].description
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

        init {
            itemView.setOnClickListener {
                onItemClick(items[adapterPosition])
            }
            val buttonSubscribe = itemView.findViewById(R.id.buttonSubscribe) as Button
            buttonSubscribe.setOnClickListener {
                onSubscribeClick(items[adapterPosition])
            }
        }
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