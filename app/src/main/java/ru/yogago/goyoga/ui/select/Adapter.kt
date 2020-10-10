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
import java.util.*

private const val TYPE_FIST_SIDE = 0
private const val TYPE_SECOND_SIDE = 1
private const val TYPE_SYMMETRIC = 2

class Adapter(private val items: List<Asana>, private val resources: Resources): RecyclerView.Adapter<Adapter.ItemViewHolder?>() {

    var onItemClick: ((Asana) -> Unit)? = null
    private val isRussianLanguage: Boolean = Locale.getDefault().language == "ru"


    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ItemViewHolder {
        lateinit var v: View
        when (i) {
            TYPE_FIST_SIDE -> v =
                LayoutInflater.from(viewGroup.context).inflate(
                    R.layout.card_view_asana_first_side,
                    viewGroup,
                    false
                )
            TYPE_SECOND_SIDE -> v =
                LayoutInflater.from(viewGroup.context).inflate(
                    R.layout.card_view_asana_second_side,
                    viewGroup,
                    false
                )
            TYPE_SYMMETRIC -> v =
                LayoutInflater.from(viewGroup.context).inflate(
                    R.layout.card_view_asana,
                    viewGroup,
                    false
                )
        }
        return ItemViewHolder(v)
    }

    override fun onBindViewHolder(itemViewHolder: ItemViewHolder, position: Int) {

//        when (getItemViewType(position)) {
//            TYPE_FIST_SIDE -> itemViewHolder.fieldsName.text = ""
//            TYPE_SECOND_SIDE -> itemViewHolder.fieldsName.text = ""
//        }

        itemViewHolder.fieldsName.text = if (isRussianLanguage) items[position].name else items[position].eng
        itemViewHolder.profilePetsUserPic.contentDescription = "" + position + "_" + itemViewHolder.profilePetsUserPic.contentDescription.toString() + "_" + items[position].eng
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
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        if (items[position].side == "first") return TYPE_FIST_SIDE
        if (items[position].side == "second") return TYPE_SECOND_SIDE
        return TYPE_SYMMETRIC
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val fieldsName = itemView.findViewById(R.id.cv_select_title) as TextView
        val profilePetsUserPic: ImageView = itemView.findViewById(R.id.cv_select_image)

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(items[adapterPosition])
            }
        }

    }

}