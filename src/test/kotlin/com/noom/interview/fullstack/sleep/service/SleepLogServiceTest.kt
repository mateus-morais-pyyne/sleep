package com.noom.interview.fullstack.sleep.service

import com.noom.interview.fullstack.sleep.db.SleepLogRepository
import com.noom.interview.fullstack.sleep.model.SleepLog
import com.noom.interview.fullstack.sleep.model.User
import com.noom.interview.fullstack.sleep.model.dto.CreateSleepLogDTO
import com.noom.interview.fullstack.sleep.model.enumeration.MorningFeeling
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

class SleepLogServiceTest {

    private val sleepLogRepository: SleepLogRepository = mock(SleepLogRepository::class.java)
    private val userService: UserService = mock(UserService::class.java)
    private val sleepLogService = SleepLogService(sleepLogRepository, userService)

    @Test
    fun `should Create SleepLog With Valid Input`() {
        val sleepLogId = UUID.randomUUID()
        val userId = UUID.randomUUID()
        val bedTime = LocalDateTime.of(2025, 6, 21, 22, 0, 0)
        val wakeUpTime = LocalDateTime.of(2025, 6, 22, 6, 0, 0)

        val user = User(
            id = userId,
            name = "John",
            age = 21,
            email = "john.doe@example.com"
        )
        val dateOfSleep = LocalDate.of(2025, 6, 21)

        val createSleepLogDTO = CreateSleepLogDTO(
            dateOfSleep = dateOfSleep,
            bedTime = LocalDateTime.now().minusHours(7),
            wakeUpTime = LocalDateTime.now(),
            morningFeeling = MorningFeeling.GOOD
        )
        val savedSleepLog = SleepLog(
            id = sleepLogId,
            dateOfSleep = bedTime.toLocalDate(),
            bedTime = bedTime,
            wakeUpTime = wakeUpTime,
            totalTimeInBed = 480,
            morningFeeling = MorningFeeling.OK,
            user = user
        )

        `when`(userService.findById(userId)).thenReturn(user)
        `when`(sleepLogRepository.save(any(SleepLog::class.java))).thenReturn(savedSleepLog)

        val result = sleepLogService.createSleepLog(createSleepLogDTO, userId)
        assertNotNull(result)
        verify(userService).findById(userId)
        verify(sleepLogRepository).save(any(SleepLog::class.java))
    }

    @Test
    fun `should find SleepLog With Valid Input`() {
        val sleepLogId = UUID.randomUUID()
        val userId = UUID.randomUUID()
        val bedTime = LocalDateTime.of(2025, 6, 21, 22, 0, 0)
        val wakeUpTime = LocalDateTime.of(2025, 6, 22, 6, 0, 0)
        val user = User(
            id = userId,
            name = "John",
            age = 21,
            email = "john.doe@example.com"
        )
        val sleepLog = SleepLog(
            id = sleepLogId,
            dateOfSleep = bedTime.toLocalDate(),
            bedTime = bedTime,
            wakeUpTime = wakeUpTime,
            totalTimeInBed = 480,
            morningFeeling = MorningFeeling.OK,
            user = user
        )
        `when`(sleepLogRepository.findById(sleepLogId)).thenReturn(Optional.of(sleepLog))
        val result = sleepLogService.findById(sleepLogId)
        assertEquals(sleepLog, result)
    }

    @Test
    fun `should throw error with invalid Input`() {
        val sleepLogId = UUID.randomUUID()
        `when`(sleepLogRepository.findById(sleepLogId)).thenReturn(Optional.empty())
        val ex = assertThrows<ResponseStatusException> {
            sleepLogService.findById(sleepLogId)
        }
        assertEquals("404 NOT_FOUND \"Sleep log not found with id: $sleepLogId\"", ex.message)
    }
}