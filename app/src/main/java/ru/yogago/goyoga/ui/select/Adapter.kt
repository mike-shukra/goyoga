package ru.yogago.goyoga.ui.select

import android.content.res.Resources
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import ru.yogago.goyoga.R
import ru.yogago.goyoga.data.AppConstants.LOG_TAG
import ru.yogago.goyoga.data.AppConstants.PHOTO_URL
import ru.yogago.goyoga.data.Asana

class Adapter(private val items: List<Asana>?, private val resources: Resources): RecyclerView.Adapter<Adapter.ItemViewHolder?>() {

    var onItemClick: ((Asana) -> Unit)? = null

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ItemViewHolder {
        val v: View = LayoutInflater.from(viewGroup.context).inflate(R.layout.card_view_asana, viewGroup, false)
        return ItemViewHolder(v)
    }

    override fun onBindViewHolder(itemViewHolder: ItemViewHolder, position: Int) {
        itemViewHolder.fieldsName.text = items!![position].name
        val patch = PHOTO_URL + items[position].photo
        Log.d(LOG_TAG, patch)
        val picasso = Picasso.get()
        picasso.setIndicatorsEnabled(false)
        picasso
            .load(patch)
            .resize(320, 214)
            .onlyScaleDown()
            .centerCrop()
            .placeholder(resources.getIdentifier("placeholder", "drawable", "ru.yogago.goyoga"))
            .into(itemViewHolder.profilePetsUserPic)
    }

    override fun getItemCount(): Int {
        return items!!.size
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val fieldsName = itemView.findViewById(R.id.cv_select_title) as TextView
        val profilePetsUserPic: ImageView = itemView.findViewById(R.id.cv_select_image)

        init {
            itemView.setOnClickListener {
                items?.get(adapterPosition)?.let { pet -> onItemClick?.invoke(pet) }
            }
        }

    }

}