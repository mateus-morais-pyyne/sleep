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
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit
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

    @Test
    fun `findLatestByUserId should return latest sleep log when exists`() {
        // Arrange
        val userId = UUID.randomUUID()
        val sleepLogId = UUID.randomUUID()
        val bedTime = LocalDateTime.of(2025, 6, 21, 22, 0, 0)
        val wakeUpTime = LocalDateTime.of(2025, 6, 22, 6, 0, 0)

        val user = User(
            id = userId,
            name = "John",
            age = 21,
            email = "john.doe@example.com"
        )

        val latestSleepLog = SleepLog(
            id = sleepLogId,
            dateOfSleep = bedTime.toLocalDate(),
            bedTime = bedTime,
            wakeUpTime = wakeUpTime,
            totalTimeInBed = 480,
            morningFeeling = MorningFeeling.OK,
            user = user
        )

        `when`(sleepLogRepository.findLatestByUserId(userId)).thenReturn(latestSleepLog)

        // Act
        val result = sleepLogService.findLatestByUserId(userId)

        // Assert
        assertNotNull(result)
        assertEquals(latestSleepLog, result)
        verify(sleepLogRepository).findLatestByUserId(userId)
    }

    @Test
    fun `findLatestByUserId should throw ResponseStatusException when no sleep logs found`() {
        // Arrange
        val userId = UUID.randomUUID()
        `when`(sleepLogRepository.findLatestByUserId(userId)).thenReturn(null)

        // Act & Assert
        val exception = assertThrows<ResponseStatusException> {
            sleepLogService.findLatestByUserId(userId)
        }

        assertEquals(HttpStatus.NOT_FOUND, exception.status)
        assertEquals("No sleep logs found for user with id: $userId", exception.reason)
        verify(sleepLogRepository).findLatestByUserId(userId)
    }

    @Test
    fun `getLast30DayAverages should return correct statistics when logs exist`() {
        // Arrange
        val userId = UUID.randomUUID()
        val endDate = LocalDate.now()
        val startDate = endDate.minusDays(29)

        val user = User(
            id = userId,
            name = "John",
            age = 21,
            email = "john.doe@example.com"
        )

        // Create test data with varied sleep patterns
        val sleepLogs = listOf(
            createSleepLog(
                LocalDateTime.of(2025, 6, 21, 22, 0),
                LocalDateTime.of(2025, 6, 22, 6, 0),
                MorningFeeling.GOOD,
                user
            ),
            createSleepLog(
                LocalDateTime.of(2025, 6, 22, 23, 0),
                LocalDateTime.of(2025, 6, 23, 7, 0),
                MorningFeeling.OK,
                user
            ),
            createSleepLog(
                LocalDateTime.of(2025, 6, 23, 22, 30),
                LocalDateTime.of(2025, 6, 24, 6, 30),
                MorningFeeling.GOOD,
                user
            )
        )

        `when`(sleepLogRepository.findByUserIdAndDateRange(userId, startDate, endDate))
            .thenReturn(sleepLogs)

        // Act
        val result = sleepLogService.getLast30DayAverages(userId)

        // Assert
        assertEquals(startDate, result.startDate)
        assertEquals(endDate, result.endDate)
        assertEquals(480, result.averageTotalTimeInBed) // 8 hours average
        assertEquals(LocalTime.of(22, 30), result.averageBedTime)
        assertEquals(LocalTime.of(6, 30), result.averageWakeUpTime)
        assertEquals(2, result.morningFeelingFrequencies?.get(MorningFeeling.GOOD))
        assertEquals(1, result.morningFeelingFrequencies?.get(MorningFeeling.OK))
        assertEquals(0, result.morningFeelingFrequencies?.get(MorningFeeling.BAD))
        assertEquals(30, result.totalDays)
        assertEquals(3, result.daysWithLogs)

        verify(sleepLogRepository).findByUserIdAndDateRange(userId, startDate, endDate)
    }

    @Test
    fun `getLast30DayAverages should return empty statistics when no logs exist`() {
        // Arrange
        val userId = UUID.randomUUID()
        val endDate = LocalDate.now()
        val startDate = endDate.minusDays(29)

        `when`(sleepLogRepository.findByUserIdAndDateRange(userId, startDate, endDate))
            .thenReturn(emptyList())

        // Act
        val result = sleepLogService.getLast30DayAverages(userId)

        // Assert
        assertEquals(startDate, result.startDate)
        assertEquals(endDate, result.endDate)
        assertEquals(0, result.averageTotalTimeInBed)
        assertEquals(null, result.averageBedTime)
        assertEquals(null, result.averageWakeUpTime)
        assertEquals(0, result.morningFeelingFrequencies?.get(MorningFeeling.GOOD))
        assertEquals(0, result.morningFeelingFrequencies?.get(MorningFeeling.OK))
        assertEquals(0, result.morningFeelingFrequencies?.get(MorningFeeling.BAD))
        assertEquals(0, result.totalDays)
        assertEquals(0, result.daysWithLogs)

        verify(sleepLogRepository).findByUserIdAndDateRange(userId, startDate, endDate)
    }

    @Test
    fun `getLast30DayAverages should handle single day data correctly`() {
        // Arrange
        val userId = UUID.randomUUID()
        val endDate = LocalDate.now()
        val startDate = endDate.minusDays(29)

        val user = User(
            id = userId,
            name = "John",
            age = 21,
            email = "john.doe@example.com"
        )

        val singleLog = createSleepLog(
            LocalDateTime.of(2025, 6, 21, 22, 0),
            LocalDateTime.of(2025, 6, 22, 6, 0),
            MorningFeeling.GOOD,
            user
        )

        `when`(sleepLogRepository.findByUserIdAndDateRange(userId, startDate, endDate))
            .thenReturn(listOf(singleLog))

        // Act
        val result = sleepLogService.getLast30DayAverages(userId)

        // Assert
        assertEquals(480, result.averageTotalTimeInBed) // 8 hours
        assertEquals(LocalTime.of(22, 0), result.averageBedTime)
        assertEquals(LocalTime.of(6, 0), result.averageWakeUpTime)
        assertEquals(1, result.morningFeelingFrequencies?.get(MorningFeeling.GOOD))
        assertEquals(1, result.daysWithLogs)

        verify(sleepLogRepository).findByUserIdAndDateRange(userId, startDate, endDate)
    }

    // Helper method to create test sleep logs
    private fun createSleepLog(
        bedTime: LocalDateTime,
        wakeUpTime: LocalDateTime,
        feeling: MorningFeeling,
        user: User
    ): SleepLog {
        return SleepLog(
            id = UUID.randomUUID(),
            dateOfSleep = bedTime.toLocalDate(),
            bedTime = bedTime,
            wakeUpTime = wakeUpTime,
            totalTimeInBed = ChronoUnit.MINUTES.between(bedTime, wakeUpTime).toInt(),
            morningFeeling = feeling,
            user = user
        )
    }
}