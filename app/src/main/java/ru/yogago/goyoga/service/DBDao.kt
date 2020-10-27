package ru.yogago.goyoga.service

import androidx.room.*
import ru.yogago.goyoga.data.*

@Dao
interface DBDao {
    @Query("SELECT * FROM settings")
    fun getSettings(): Settings?

    @Query("SELECT * FROM asana")
    fun getAsanas(): List<Asana>

    @Query("SELECT * FROM asana WHERE id = :id")
    fun getAsana(id: Int): Asana

    @Query("SELECT * FROM userData")
    fun getUserData(): UserData

    @Query("SELECT * FROM actionState")
    fun getActionState(): ActionState

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSettings(settings: Settings): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAsanas(asanas: List<Asana>): List<Long>

    @Query("SELECT * FROM userData")
    fun loadUserData(): UserData

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUserData(user: UserData): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertActionState(actionState: ActionState): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertToken(token: Token): Long

    @Query("SELECT * FROM Token WHERE id = 0")
    fun getToken(): Token?

    @Query("DELETE FROM Token")
    fun deleteToken(): Int

    @Query("DELETE FROM ActionState")
    fun deleteActionState(): Int

    @Query("DELETE FROM Asana")
    fun deleteAsanas(): Int

    @Query("DELETE FROM UserData")
    fun deleteUserData(): Int

    @Query("UPDATE userdata SET first_name = :name WHERE id = :id")
    fun insertUserName(name: String, id: Long): Int

    @Query("UPDATE settings SET proportionately = :proportionately WHERE id = 0")
    fun updateSettingsProportionately(proportionately: Float): Int

    @Query("UPDATE settings SET addTime = :addTime WHERE id = 0")
    fun updateSettingsAddTime(addTime: Int): Int

    @Query("UPDATE settings SET language = :language WHERE id = 0")
    fun updateSettingsLanguage(language: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPurchase(purchase: PurchaseItem): Long

    @Query("UPDATE PurchaseItem SET purchaseToken = :purchaseToken, purchaseState = :purchaseState, acknowledged = :acknowledged WHERE productId = :productId")
    fun updatePurchase(productId: Long, purchaseToken: String, purchaseState: Int, acknowledged: Boolean): Int

}
