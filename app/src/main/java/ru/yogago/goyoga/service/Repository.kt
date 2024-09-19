package ru.yogago.goyoga.service

import ru.yogago.goyoga.data.ActionState
import ru.yogago.goyoga.data.Asana
import ru.yogago.goyoga.data.PurchaseItem
import ru.yogago.goyoga.data.Settings
import ru.yogago.goyoga.data.UserData

open class Repository(private val dbDao: DBDao) {

    suspend fun getSettings(): Settings {
        return dbDao.getSettings()
    }

    suspend fun getDataFromDB(): List<Asana> {
        return dbDao.getAsanas()
    }

    suspend fun getAsana(id: Int): Asana {
        return dbDao.getAsana(id)
    }

    suspend fun getUserData(): UserData {
        return dbDao.getUserData()
    }

    suspend fun getActionState(): ActionState {
        return dbDao.getActionState()
    }

    suspend fun insertSettings(settings: Settings): Long {
        return dbDao.insertSettings(settings)
    }

    suspend fun insertAsanas(asanas: List<Asana>): List<Long> {
        return dbDao.insertAsanas(asanas)
    }

    suspend fun loadUserData(): UserData {
        return dbDao.getUserData()
    }

    suspend fun insertUserData(user: UserData): Long {
        return dbDao.insertUserData(user)
    }

    suspend fun insertActionState(actionState: ActionState): Long {
        return dbDao.insertActionState(actionState)
    }

    suspend fun deleteActionState(): Int {
        return dbDao.deleteActionState()
    }

    suspend fun deleteAsanas(): Int {
        return dbDao.deleteAsanas()
    }

    suspend fun deleteUserData(): Int {
        return dbDao.deleteUserData()
    }

    suspend fun deleteSettings(): Int {
        return dbDao.deleteSettings()
    }

    suspend fun insertUserName(name: String, id: Long): Int {
        return dbDao.insertUserName(name, id)
    }

    suspend fun updateSettingsProportionately(proportionately: Float): Int {
        return dbDao.updateSettingsProportionately(proportionately)
    }

    suspend fun updateSettingsAddTime(addTime: Int): Int {
        return dbDao.updateSettingsAddTime(addTime)
    }

    suspend fun updateSettingsLanguage(language: String): Int {
        return dbDao.updateSettingsLanguage(language)
    }

    suspend fun updateSettingsIsSpeakAsanaName(isSpeakAsanaName: Boolean): Int {
        return dbDao.updateSettingsIsSpeakAsanaName(isSpeakAsanaName)
    }

    suspend fun insertPurchase(purchase: PurchaseItem): Long {
        return dbDao.insertPurchase(purchase)
    }

    suspend fun updatePurchase(productId: Long, purchaseToken: String, purchaseState: Int, acknowledged: Boolean): Int {
        return dbDao.updatePurchase(productId, purchaseToken, purchaseState, acknowledged)
    }

    suspend fun updateHowToSort(value: Boolean): Int {
        return dbDao.updateHowToSort(value)
    }
}