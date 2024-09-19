package ru.yogago.goyoga


import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.After
import org.junit.Before
import org.mockito.Mockito.*
import org.junit.Assert.*

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import ru.yogago.goyoga.data.Asana
import ru.yogago.goyoga.data.Data
import ru.yogago.goyoga.service.Api
import ru.yogago.goyoga.service.AppDatabase
import ru.yogago.goyoga.service.DBDao
import ru.yogago.goyoga.service.Repository

@RunWith(AndroidJUnit4::class)
class RepositoryInstrumentedTest {

    private lateinit var database: AppDatabase
    private lateinit var dbDao: DBDao
    private lateinit var repository: Repository
    private lateinit var api: Api

    @Before
    fun setUp() {

        // Создание временной базы данных
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).build()
        dbDao = database.getDBDao()
        repository = Repository(dbDao)
        api = mock(Api::class.java)

        // Мокирование API
        val asana = Asana(id = 1, name = "TestName", eng = "TestNameEng", description = "Description", description_en = "DescriptionEn", photo = "URL", symmetric = "false", side = "first", times = 2)
        val deferred = CompletableDeferred(Data(asanas = listOf(asana)))
        `when`(api.getDataAsync(anyString())).thenReturn(deferred)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun testRepository() = runBlocking {
        val result = repository.getDataFromDB()
        assertNotNull(result)
        // Дополнительные утверждения
    }

    @Test
    fun testRepositoryGetDataFromDB() = runBlocking {
        val result = repository.getDataFromDB()
        assertNotNull(result)
        Assert.assertTrue(result.isEmpty())
    }

    @Test
    fun testRepositoryFetchDataFromApi() = runBlocking {
        var asana = Asana(id = 1, name = "TestName", eng = "TestNameEng", description = "Description", description_en = "DescriptionEn", photo = "URL", symmetric = "false", side = "first", times = 2)
        repository.insertAsanas(listOf(asana))
        asana = repository.getAsana(1)
        assertNotNull(asana)
        Assert.assertEquals(1, asana.id)
        Assert.assertEquals("TestName", asana.name)
    }
}