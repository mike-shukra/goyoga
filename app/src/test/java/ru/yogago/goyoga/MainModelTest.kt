//package ru.yogago.goyoga
//
//import android.app.Application
//import android.content.Context
//import androidx.arch.core.executor.testing.InstantTaskExecutorRule
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
//import org.mockito.MockitoAnnotations
//import ru.yogago.goyoga.data.ActionState
//import ru.yogago.goyoga.data.Data
//import ru.yogago.goyoga.data.ParametersDTO
//import ru.yogago.goyoga.data.Settings
//import ru.yogago.goyoga.data.UserData
//
//
//class MainModelTest {
//    @get:Rule
//    val instantExecutorRule = InstantTaskExecutorRule()
//
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
//
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
//        val testData = Data(
//            asanas = listOf(),
//            settings = Settings(),
//            actionState = ActionState(),
//            userData = userData
//        )
//
//
//        // Мокируем Deferred<Data>
//        val deferredData = CompletableDeferred(testData)
//
//    }
//
//}