package ru.yogago.goyoga.service

import androidx.room.*
import ru.yogago.goyoga.data.*

@Dao
interface DBDao {
    @Query("SELECT * FROM asana")
    fun getAsanas(): List<Asana>

    @Query("SELECT * FROM userdata")
    fun getUserData(): UserData

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAsanas(pets: List<Asana>): List<Long>

    @Query("SELECT * FROM userData")
    fun loadUser(): UserData

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: UserData): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertToken(token: Token): Long

    @Query("SELECT * FROM Token WHERE id = 0")
    fun getToken(): Token?

    @Query("DELETE FROM Token")
    fun deleteToken(): Int

    @Query("DELETE FROM UserData")
    fun deleteUser(): Int

}
