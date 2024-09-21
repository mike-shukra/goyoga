package ru.yogago.goyoga.service

import kotlinx.coroutines.Deferred
import ru.yogago.goyoga.data.ActionState
import ru.yogago.goyoga.data.AppConstants
import ru.yogago.goyoga.data.Asana
import ru.yogago.goyoga.data.BooleanDTO
import ru.yogago.goyoga.data.Data
import ru.yogago.goyoga.data.ParametersDTO
import ru.yogago.goyoga.data.PurchaseItem
import ru.yogago.goyoga.data.Settings
import ru.yogago.goyoga.data.UserData
import java.io.IOException

open class Repository(private val dbDao: DBDao, private val api: Api) {

    private val logger: Logger = AndroidLogger()

    suspend fun getSettings(): Settings {
        return dbDao.getSettings()
    }

    suspend fun getAsanas(): List<Asana> {
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

    @Throws(Exception::class)
    suspend fun deleteAllData() {
        dbDao.deleteAsanas()
        dbDao.deleteUserData()
        dbDao.deleteSettings()
        dbDao.deleteActionState()
    }

    @Throws(Exception::class)
    suspend fun insertData(data: Data) {
        dbDao.insertAsanas(data.asanas!!)
        dbDao.insertSettings(data.settings!!)
        dbDao.insertUserData(data.userData!!)
        dbDao.insertActionState(data.actionState!!)
    }

    @Throws(Exception::class)
    suspend fun createNewSequence(token: String, parametersDTO: ParametersDTO): Boolean {

        val deferred = api.createAsync(token, parametersDTO)
        val data: Data = deferred.await()

        val del = dbDao.deleteAsanas()
        val insA = dbDao.insertAsanas(data.asanas!!)
        val insS = dbDao.insertSettings(data.settings!!)
        val insAs = dbDao.insertActionState(data.actionState!!)
        val insD = dbDao.insertUserData(data.userData!!)
        logger.d(
            AppConstants.LOG_TAG,
            "Repository - createNewSequence del: $del, insA: $insA, insAs: $insAs, insS: $insS, idsD: $insD"
        )
        return true
    }

    @Throws(Exception::class)
    suspend fun updateParameters(token: String,parametersDTO: ParametersDTO)  {
        val deferred = api.updateParameters(token, parametersDTO)
        val data: Data = deferred.await()
        logger.d(AppConstants.LOG_TAG, "Repository - updateParameters data: $data")
        dbDao.deleteAsanas()
        dbDao.insertAsanas(data.asanas!!)
    }

    @Throws(Exception::class)
    suspend fun isUserExist(firebaseToken: String): BooleanDTO {
        return api.isUserExist(firebaseToken).await()
    }

    @Throws(Exception::class)
    suspend fun signUp(firebaseToken: String): BooleanDTO {
        return api.signUp(firebaseToken).await()
    }

    @Throws(Exception::class)
    suspend fun getDataAsync(firebaseToken: String): Data {
        return api.getDataAsync(firebaseToken).await()
    }
}