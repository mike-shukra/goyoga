package ru.yogago.goyoga.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PurchaseItem (
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    val productId: String,
    val purchaseToken: String,
    val purchaseState: Int,
    val acknowledged: Boolean
)