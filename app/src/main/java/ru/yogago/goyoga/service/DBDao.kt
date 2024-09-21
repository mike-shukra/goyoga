package ru.yogago.goyoga.service

import androidx.room.*
import ru.yogago.goyoga.data.*

@Dao
interface DBDao {
    @Query("SELECT * FROM settings")
    suspend fun getSettings(): Settings

    @Query("SELECT * FROM asana")
    suspend fun getAsanas(): List<Asana>

    @Query("SELECT * FROM asana WHERE id = :id")
    suspend fun getAsana(id: Int): Asana

    @Query("SELECT * FROM userData")
    suspend fun getUserData(): UserData

    @Query("SELECT * FROM actionState")
    suspend fun getActionState(): ActionState

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(settings: Settings): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAsanas(asanas: List<Asana>): List<Long>

    @Query("SELECT * FROM userData")
    suspend fun loadUserData(): UserData

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserData(user: UserData): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActionState(actionState: ActionState): Long
    
    @Query("DELETE FROM ActionState")
    suspend fun deleteActionState(): Int

    @Query("DELETE FROM Asana")
    suspend fun deleteAsanas(): Int

    @Query("DELETE FROM UserData")
    suspend fun deleteUserData(): Int

    @Query("DELETE FROM Settings")
    suspend fun deleteSettings(): Int

    @Query("UPDATE userdata SET first_name = :name WHERE id = :id")
    suspend fun insertUserName(name: String, id: Long): Int

    @Query("UPDATE settings SET proportionately = :proportionately WHERE id = 0")
    suspend fun updateSettingsProportionately(proportionately: Float): Int

    @Query("UPDATE settings SET addTime = :addTime WHERE id = 0")
    suspend fun updateSettingsAddTime(addTime: Int): Int

    @Query("UPDATE settings SET language = :language WHERE id = 0")
    suspend fun updateSettingsLanguage(language: String): Int

    @Query("UPDATE settings SET speakAsanaName = :isSpeakAsanaName WHERE id = 0")
    suspend fun updateSettingsIsSpeakAsanaName(isSpeakAsanaName: Boolean): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPurchase(purchase: PurchaseItem): Long

    @Query("UPDATE PurchaseItem SET purchaseToken = :purchaseToken, purchaseState = :purchaseState, acknowledged = :acknowledged WHERE productId = :productId")
    suspend fun updatePurchase(productId: Long, purchaseToken: String, purchaseState: Int, acknowledged: Boolean): Int
    @Query("UPDATE userdata SET sideBySideSort = :value WHERE id = 0")
    suspend fun updateHowToSort(value: Boolean): Int

}
