package ru.yogago.goyoga

import kotlinx.coroutines.test.runTest
    import org.junit.Test
    import org.junit.Assert.*
class CoroutineTest {

    @Test
    fun `test suspend function`() = runTest {
        val result = suspendFunction()
        assertEquals("Expected Result", result)
    }

    private suspend fun suspendFunction(): String {
        // Пример асинхронной работы
        return "Expected Result"
    }

}