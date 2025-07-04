package com.noom.interview.fullstack.sleep.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.noom.interview.fullstack.sleep.commons.Util
import com.noom.interview.fullstack.sleep.model.SleepLog
import com.noom.interview.fullstack.sleep.model.User
import com.noom.interview.fullstack.sleep.model.dto.CreateSleepLogDTO
import com.noom.interview.fullstack.sleep.model.dto.SleepLogAverageDTO
import com.noom.interview.fullstack.sleep.model.enumeration.MorningFeeling
import com.noom.interview.fullstack.sleep.service.SleepLogService
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

@WebMvcTest(SleepLogController::class)
class SleepLogControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockBean
    private lateinit var sleepLogService: SleepLogService

    private var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")

    private var formatterTime = DateTimeFormatter.ofPattern("HH:mm:ss")

    @Test
    fun `createSleepLog should return CREATED status and valid response on success`() {
        // Arrange
        val userId = UUID.randomUUID()
        val dateOfSleep = LocalDate.of(2025, 6, 21)
        val bedTime = LocalDateTime.of(2025, 6, 21, 22, 0)
        val wakeUpTime = LocalDateTime.of(2025, 6, 22, 6, 0)
        val morningFeeling = MorningFeeling.OK
        val user = User(userId, "John", 21, "email@email.com")
        
        val createSleepLogDTO = CreateSleepLogDTO(
            dateOfSleep = dateOfSleep,
            bedTime = bedTime,
            wakeUpTime = wakeUpTime,
            morningFeeling = morningFeeling
        )

        val totalTimeInBed = Util.getTotalTimeInBed(createSleepLogDTO)

        val createdSleepLog = SleepLog(
            id = UUID.randomUUID(),
            dateOfSleep = createSleepLogDTO.bedTime.toLocalDate(),
            bedTime = createSleepLogDTO.bedTime,
            wakeUpTime = createSleepLogDTO.wakeUpTime,
            totalTimeInBed = totalTimeInBed,
            morningFeeling = createSleepLogDTO.morningFeeling,
            user = user
        )

        whenever(sleepLogService.createSleepLog(any(), any())).thenReturn(createdSleepLog)

        // Act & Assert
        mockMvc.perform(
            post("/api/sleep-logs/$userId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createSleepLogDTO))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").isNotEmpty)
            .andExpect(jsonPath("$.bedTime", equalTo(createSleepLogDTO.bedTime.format(formatter))))
            .andExpect(jsonPath("$.wakeUpTime", equalTo(createSleepLogDTO.wakeUpTime.format(formatter))))
            .andExpect(jsonPath("$.dateOfSleep", equalTo(createSleepLogDTO.dateOfSleep.toString())))
            .andExpect(jsonPath("$.totalTimeInBed", equalTo(totalTimeInBed)))
            .andExpect(jsonPath("$.morningFeeling", equalTo(morningFeeling.toString())))
            .andExpect(jsonPath("$.userId", equalTo(userId.toString())))
    }

    @Test
    fun `getSleepLogById should return OK status and the SleepLogDTO when valid`() {
        // Arrange
        val userId = UUID.randomUUID()
        val sleepLogId = UUID.randomUUID()

        val bedTime = LocalDateTime.of(2025, 6, 21, 22, 0, 0)
        val wakeUpTime = LocalDateTime.of(2025, 6, 22, 6, 0, 0)

        val user = User(userId, "John", 21, "email@email.com")
        val sleepLog = SleepLog(
            id = sleepLogId,
            dateOfSleep = bedTime.toLocalDate(),
            bedTime = bedTime,
            wakeUpTime = wakeUpTime,
            totalTimeInBed = 480,
            morningFeeling = MorningFeeling.OK,
            user = user
        )

        whenever(sleepLogService.findById(sleepLogId)).thenReturn(sleepLog)

        // Act & Assert
        mockMvc.perform(
            get("/api/sleep-logs/$userId/$sleepLogId")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id", equalTo(sleepLog.id.toString())))
            .andExpect(jsonPath("$.bedTime", equalTo(sleepLog.bedTime.format(formatter))))
            .andExpect(jsonPath("$.wakeUpTime", equalTo(sleepLog.wakeUpTime.format(formatter))))
            .andExpect(jsonPath("$.dateOfSleep", equalTo(sleepLog.dateOfSleep.toString())))
            .andExpect(jsonPath("$.totalTimeInBed", equalTo(sleepLog.totalTimeInBed)))
            .andExpect(jsonPath("$.morningFeeling", equalTo(sleepLog.morningFeeling.name)))
            .andExpect(jsonPath("$.userId", equalTo(sleepLog.user.id.toString())))
    }

    @Test
    fun `getSleepLogById should return NOT_FOUND status when userId does not match`() {
        // Arrange
        val userId = UUID.randomUUID()
        val sleepLogId = UUID.randomUUID()

        whenever(sleepLogService.findById(sleepLogId)).thenThrow(ResponseStatusException(HttpStatus.NOT_FOUND))

        // Act & Assert
        mockMvc.perform(
            get("/api/sleep-logs/$userId/$sleepLogId")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `getLatestSleepLog should return OK status and latest sleep log when available`() {
        // Arrange
        val userId = UUID.randomUUID()
        val sleepLogId = UUID.randomUUID()

        val bedTime = LocalDateTime.of(2025, 6, 21, 22, 0, 0)
        val wakeUpTime = LocalDateTime.of(2025, 6, 22, 6, 0, 0)

        val user = User(userId, "John", 21, "email@email.com")
        val latestSleepLog = SleepLog(
            id = sleepLogId,
            dateOfSleep = bedTime.toLocalDate(),
            bedTime = bedTime,
            wakeUpTime = wakeUpTime,
            totalTimeInBed = 480,
            morningFeeling = MorningFeeling.OK,
            user = user
        )

        whenever(sleepLogService.findLatestByUserId(userId)).thenReturn(latestSleepLog)

        // Act & Assert
        mockMvc.perform(
            get("/api/sleep-logs/$userId/latest")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id", equalTo(latestSleepLog.id.toString())))
            .andExpect(jsonPath("$.bedTime", equalTo(latestSleepLog.bedTime.format(formatter))))
            .andExpect(jsonPath("$.wakeUpTime", equalTo(latestSleepLog.wakeUpTime.format(formatter))))
            .andExpect(jsonPath("$.dateOfSleep", equalTo(latestSleepLog.dateOfSleep.toString())))
            .andExpect(jsonPath("$.totalTimeInBed", equalTo(latestSleepLog.totalTimeInBed)))
            .andExpect(jsonPath("$.morningFeeling", equalTo(latestSleepLog.morningFeeling.name)))
            .andExpect(jsonPath("$.userId", equalTo(latestSleepLog.user.id.toString())))
    }

    @Test
    fun `getLatestSleepLog should return NOT_FOUND status when no sleep logs exist for user`() {
        // Arrange
        val userId = UUID.randomUUID()

        whenever(sleepLogService.findLatestByUserId(userId))
            .thenThrow(ResponseStatusException(HttpStatus.NOT_FOUND))

        // Act & Assert
        mockMvc.perform(
            get("/api/sleep-logs/$userId/latest")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `getLast30DaysStatistics should return OK status and valid statistics`() {
        // Arrange
        val userId = UUID.randomUUID()
        val endDate = LocalDate.now()
        val startDate = endDate.minusDays(29)

        val averageBedTime = LocalTime.of(22, 30)
        val averageWakeUpTime = LocalTime.of(6, 30)

        val statistics = SleepLogAverageDTO(
            startDate = startDate,
            endDate = endDate,
            averageTotalTimeInBed = 480,
            averageBedTime = averageBedTime,
            averageWakeUpTime = averageWakeUpTime,
            morningFeelingFrequencies = mapOf(
                MorningFeeling.GOOD to 15,
                MorningFeeling.OK to 10,
                MorningFeeling.BAD to 2
            ),
            totalDays = 30,
            daysWithLogs = 27
        )

        whenever(sleepLogService.getLast30DayAverages(userId)).thenReturn(statistics)

        // Act & Assert
        mockMvc.perform(
            get("/api/sleep-logs/$userId/statistics/last-30-days")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.startDate", equalTo(startDate.toString())))
            .andExpect(jsonPath("$.endDate", equalTo(endDate.toString())))
            .andExpect(jsonPath("$.averageTotalTimeInBed", equalTo(480)))
            .andExpect(jsonPath("$.averageBedTime", equalTo(averageBedTime.format(formatterTime))))
            .andExpect(jsonPath("$.averageWakeUpTime", equalTo(averageWakeUpTime.format(formatterTime))))
            .andExpect(jsonPath("$.morningFeelingFrequencies.GOOD", equalTo(15)))
            .andExpect(jsonPath("$.morningFeelingFrequencies.OK", equalTo(10)))
            .andExpect(jsonPath("$.morningFeelingFrequencies.BAD", equalTo(2)))
            .andExpect(jsonPath("$.totalDays", equalTo(30)))
            .andExpect(jsonPath("$.daysWithLogs", equalTo(27)))
    }

    @Test
    fun `getLast30DaysStatistics should return OK status with zero values when no data available`() {
        // Arrange
        val userId = UUID.randomUUID()
        val endDate = LocalDate.now()
        val startDate = endDate.minusDays(29)

        val emptyStatistics = SleepLogAverageDTO(
            startDate = startDate,
            endDate = endDate,
            averageTotalTimeInBed = 0,
            averageBedTime = LocalTime.MIDNIGHT,
            averageWakeUpTime = LocalTime.MIDNIGHT,
            morningFeelingFrequencies = mapOf(
                MorningFeeling.GOOD to 0,
                MorningFeeling.OK to 0,
                MorningFeeling.BAD to 0
            ),
            totalDays = 30,
            daysWithLogs = 0
        )

        whenever(sleepLogService.getLast30DayAverages(userId)).thenReturn(emptyStatistics)

        // Act & Assert
        mockMvc.perform(
            get("/api/sleep-logs/$userId/statistics/last-30-days")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.startDate", equalTo(startDate.toString())))
            .andExpect(jsonPath("$.endDate", equalTo(endDate.toString())))
            .andExpect(jsonPath("$.averageTotalTimeInBed", equalTo(0)))
            .andExpect(jsonPath("$.averageBedTime", equalTo("00:00:00")))
            .andExpect(jsonPath("$.averageWakeUpTime", equalTo("00:00:00")))
            .andExpect(jsonPath("$.morningFeelingFrequencies.GOOD", equalTo(0)))
            .andExpect(jsonPath("$.morningFeelingFrequencies.OK", equalTo(0)))
            .andExpect(jsonPath("$.morningFeelingFrequencies.BAD", equalTo(0)))
            .andExpect(jsonPath("$.totalDays", equalTo(30)))
            .andExpect(jsonPath("$.daysWithLogs", equalTo(0)))
    }
}