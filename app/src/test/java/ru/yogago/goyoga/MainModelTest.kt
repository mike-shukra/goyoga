//package ru.yogago.goyoga
//
//import android.app.Application
//import android.content.Context
//import androidx.arch.core.executor.testing.InstantTaskExecutorRule
//import androidx.room.Room
//import androidx.test.core.app.ApplicationProvider
//import kotlinx.coroutines.CompletableDeferred
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import kotlinx.coroutines.runBlocking
//import kotlinx.coroutines.test.StandardTestDispatcher
//import kotlinx.coroutines.test.runBlockingTest
//import kotlinx.coroutines.test.setMain
//import org.junit.After
//import org.junit.Before
//import org.junit.Test
//import org.mockito.Mockito.*
//import org.junit.Assert.*
//import org.junit.Rule
//import org.junit.runner.manipulation.Ordering
//import org.mockito.MockitoAnnotations
//import ru.yogago.goyoga.data.ActionState
//import ru.yogago.goyoga.data.Asana
//import ru.yogago.goyoga.data.Data
//import ru.yogago.goyoga.data.ParametersDTO
//import ru.yogago.goyoga.data.Settings
//import ru.yogago.goyoga.data.UserData
//import ru.yogago.goyoga.model.MainModel
//import ru.yogago.goyoga.service.Api
//import ru.yogago.goyoga.service.AppDatabase
//import ru.yogago.goyoga.service.DBDao
//import ru.yogago.goyoga.service.DataBase
//import ru.yogago.goyoga.service.Repository
//import ru.yogago.goyoga.ui.profile.ProfileViewModel
//import java.io.IOException
//
//class MainModelTest {
//    @get:Rule
//    val instantExecutorRule = InstantTaskExecutorRule()
//
//    private lateinit var profileViewModel: ProfileViewModel
//    private lateinit var db: AppDatabase
//    private lateinit var dbDao: DBDao
//    private lateinit var repository: Repository
//    private lateinit var api: Api
//    private lateinit var mockContext: Context
//    private lateinit var mockApplication: Application
//    private val testDispatcher = StandardTestDispatcher()
//
//    @OptIn(ExperimentalCoroutinesApi::class)
//    @Before
//    fun setUp() = runBlocking {
//        MockitoAnnotations.initMocks(this)
//        mockApplication = mock(Application::class.java)
//        mockContext = mock(Context::class.java)
//
//        db = Room.inMemoryDatabaseBuilder(
//            mockContext,
//            AppDatabase::class.java
//        ).build()
//
//
//        dbDao = db.getDBDao()
//        repository = Repository(dbDao)
//        api = mock(Api::class.java)
//
//        val asana = Asana(id = 1, name = "TestName", eng = "TestNameEng", description = "Description", description_en = "DescriptionEn", photo = "URL", symmetric = "false", side = "first", times = 2)
//
//        // Мокируем вызов API с Deferred
//        val deferred = CompletableDeferred(Data(asanas = listOf(asana)))
//        `when`(api.getDataAsync(anyString())).thenReturn(deferred)
//
//        // Мокируем вызов Room
//        `when`(repository.getDataFromDB()).thenReturn(emptyList())
//
//
////        profileViewModel = ProfileViewModel(mockApplication)
////        profileViewModel.setModel()
//        Dispatchers.setMain(testDispatcher)
//    }
//
//    @OptIn(ExperimentalCoroutinesApi::class)
//    @Test
//    fun `test successful data processing`() = runBlockingTest {
//        // Создаем тестовые данные
//        val parametersDTO = ParametersDTO(
//            now = 1,
//            allTime = 0,
//            allCount = 0,
//            level = "EASY",
//            proportionally = 1.0f,
//            addTime = 30,
//            dangerKnee = true,
//            dangerLoins = true,
//            dangerNeck = true,
//            inverted = false,
//            sideBySideSort = false,
//            System.currentTimeMillis()
//        )
//
//        val userData = UserData(
//            id = 0,
//            email = "test@email.com",
//            first_name = "test name",
//            now = 1,
//            allTime = 0,
//            allCount = 0,
//            level = 0,
//            dangerknee = false,
//            dangerloins = false,
//            dangerneck = false,
//            inverted = false,
//            sideBySideSort = false,
//            date = System.currentTimeMillis().toString()
//        )
//
//        val testData = Data(asanas = listOf(), settings = Settings(), actionState = ActionState(), userData = userData)
//
//
//        // Мокируем Deferred<Data>
//        val deferredData = CompletableDeferred(testData)
//
////        // Настраиваем моки
////        `when`(apiFactory.createAsync(anyString(), parametersDTO)).thenReturn(deferredData)
////        `when`(dbDao.deleteAsanas()).thenReturn(1)
////        `when`(dbDao.insertAsanas(testData.asanas!!)).thenReturn(listOf())
////        `when`(dbDao.insertSettings(testData.settings!!)).thenReturn(1)
////        `when`(dbDao.insertActionState(testData.actionState!!)).thenReturn(1)
////        `when`(dbDao.insertUserData(testData.userData!!)).thenReturn(1)
////
////        // Вызываем тестируемую функцию
////        mainModel.create(1, 1.0f, 30, true, true, true, false, false)
////
////        // Проверяем, что были вызваны все нужные методы
////        verify(dbDao).deleteAsanas()
////        verify(dbDao).insertAsanas(anyList())
////        verify(dbDao).insertSettings(Settings())
////        verify(dbDao).insertActionState(ActionState())
////        verify(dbDao).insertUserData(userData)
////
////        // Проверяем, что обновился profileViewModel.done
////        verify(profileViewModel).done.postValue(true)
//    }
//
////    @Test
////    fun `test exception handling`() = runBlockingTest {
////        // Настраиваем mock API так, чтобы выбрасывал IOException
////        `when`(apiFactory.createAsync(anyString(), any(ParametersDTO::class.java))).thenThrow(
////            IOException::class.java)
////
////        // Вызываем тестируемую функцию
////        mainModel.create(1, 1.0f, 30, true, true, true, false, false)
////
////        // Проверяем, что profileViewModel.error получил значение
////        verify(profileViewModel).error.postValue(anyString())
////    }
//@After
//fun tearDown() {
//    // Закрываем базу данных после завершения тестов
//    db.close()
//}
//
//}