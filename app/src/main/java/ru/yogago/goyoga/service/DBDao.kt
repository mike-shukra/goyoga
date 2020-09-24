package ru.yogago.goyoga.service

import androidx.room.*
import ru.yogago.goyoga.data.*

@Dao
interface DBDao {
    @Query("SELECT * FROM asana")
    fun getAsanas(): List<Asana>

    @Query("SELECT * FROM userdata")
    fun getUserData(): UserData

    @Query("SELECT * FROM actionstate")
    fun getActionState(): ActionState

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAsanas(pets: List<Asana>): List<Long>

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

}
