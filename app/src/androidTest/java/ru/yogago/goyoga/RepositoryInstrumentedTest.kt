package ru.yogago.goyoga


import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.After
import org.junit.Before
import org.junit.Assert.*

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.mockito.Mockito.mock
import ru.yogago.goyoga.data.Asana
import ru.yogago.goyoga.service.Api
import ru.yogago.goyoga.service.AppDatabase
import ru.yogago.goyoga.service.DBDao
import ru.yogago.goyoga.service.Repository

@RunWith(AndroidJUnit4::class)
class RepositoryInstrumentedTest {

    private lateinit var database: AppDatabase
    private lateinit var dbDao: DBDao
    private lateinit var api : Api
    private lateinit var repository: Repository

    @Before
    fun setUp() {

        // Создание временной базы данных
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).build()
        dbDao = database.getDBDao()

        api = mock(Api::class.java)

        repository = Repository(dbDao, api)

    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun testRepository() = runBlocking   {
        val result = repository.getAsanas()
        assertNotNull(result)
        // Дополнительные утверждения
    }

    @Test
    fun testRepositoryGetDataFromDB() = runBlocking   {
        val result = repository.getAsanas()
        assertNotNull(result)
        Assert.assertTrue(result.isEmpty())
    }

    @Test
    fun testRepositoryFetchDataFromApi() = runBlocking   {
        var asana = Asana(id = 1, name = "TestName", eng = "TestNameEng", description = "Description", description_en = "DescriptionEn", photo = "URL", symmetric = "false", side = "first", times = 2)
        repository.insertAsanas(listOf(asana))
        asana = repository.getAsana(1)
        assertNotNull(asana)
        Assert.assertEquals(1, asana.id)
        Assert.assertEquals("TestName", asana.name)
    }

    //        // Настраиваем моки
//        `when`(apiFactory.createAsync(anyString(), parametersDTO)).thenReturn(deferredData)
//        `when`(dbDao.deleteAsanas()).thenReturn(1)
//        `when`(dbDao.insertAsanas(testData.asanas!!)).thenReturn(listOf())
//        `when`(dbDao.insertSettings(testData.settings!!)).thenReturn(1)
//        `when`(dbDao.insertActionState(testData.actionState!!)).thenReturn(1)
//        `when`(dbDao.insertUserData(testData.userData!!)).thenReturn(1)
//
//        // Вызываем тестируемую функцию
//        mainModel.create(1, 1.0f, 30, true, true, true, false, false)
//
//        // Проверяем, что были вызваны все нужные методы
//        verify(dbDao).deleteAsanas()
//        verify(dbDao).insertAsanas(anyList())
//        verify(dbDao).insertSettings(Settings())
//        verify(dbDao).insertActionState(ActionState())
//        verify(dbDao).insertUserData(userData)
//
//        // Проверяем, что обновился profileViewModel.done
//        verify(profileViewModel).done.postValue(true)


//    @Test
//    fun `test exception handling`() = runBlockingTest {
//        // Настраиваем mock API так, чтобы выбрасывал IOException
//        `when`(apiFactory.createAsync(anyString(), any(ParametersDTO::class.java))).thenThrow(
//            IOException::class.java)
//
//        // Вызываем тестируемую функцию
//        mainModel.create(1, 1.0f, 30, true, true, true, false, false)
//
//        // Проверяем, что profileViewModel.error получил значение
//        verify(profileViewModel).error.postValue(anyString())
//    }


}