package ru.yogago.goyoga.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PurchaseItem (
    @PrimaryKey(autoGenerate = true) var id: Long? = null,
    var productId: String,
    var purchaseToken: String,
    var purchaseState: Int,
    var acknowledged: Boolean
)