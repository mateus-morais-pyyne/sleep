package com.noom.interview.fullstack.sleep.service

import com.noom.interview.fullstack.sleep.commons.Util
import com.noom.interview.fullstack.sleep.db.SleepLogRepository
import com.noom.interview.fullstack.sleep.model.SleepLog
import com.noom.interview.fullstack.sleep.model.dto.CreateSleepLogDTO
import com.noom.interview.fullstack.sleep.model.dto.SleepLogAverageDTO
import com.noom.interview.fullstack.sleep.model.enumeration.MorningFeeling
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

@Service
class SleepLogService(
    private val sleepLogRepository: SleepLogRepository,
    private val userService: UserService
) {
    
    @Transactional
    fun createSleepLog(createSleepLogDTO: CreateSleepLogDTO, userId: UUID): SleepLog {
        val user = userService.findById(userId)

        val totalTimeInBed = Util.getTotalTimeInBed(createSleepLogDTO)

        val sleepLog = SleepLog(
            dateOfSleep = createSleepLogDTO.bedTime.toLocalDate(),
            bedTime = createSleepLogDTO.bedTime,
            wakeUpTime = createSleepLogDTO.wakeUpTime,
            totalTimeInBed = totalTimeInBed,
            morningFeeling = createSleepLogDTO.morningFeeling,
            user = user
        )
        
        return sleepLogRepository.save(sleepLog)
    }
    
    @Transactional(readOnly = true)
    fun findById(id: UUID): SleepLog {
        return sleepLogRepository.findByIdOrNull(id)
            ?: throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Sleep log not found with id: $id"
            )
    }

    @Transactional(readOnly = true)
    fun findLatestByUserId(userId: UUID): SleepLog {
        return sleepLogRepository.findLatestByUserId(userId)
            ?: throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "No sleep logs found for user with id: $userId"
            )
    }

    @Transactional(readOnly = true)
    fun getLast30DayAverages(userId: UUID): SleepLogAverageDTO {
        val endDate = LocalDate.now()
        val startDate = endDate.minusDays(29)

        val sleepLogs = sleepLogRepository.findByUserIdAndDateRange(userId, startDate, endDate)

        if (sleepLogs.isEmpty()) {
            return SleepLogAverageDTO(
                startDate = startDate,
                endDate = endDate,
                averageTotalTimeInBed = 0,
                averageBedTime = null,
                averageWakeUpTime = null,
                morningFeelingFrequencies = MorningFeeling.values().associateWith { 0 },
                totalDays = 0,
                daysWithLogs = 0
            )

        }

        // Calculate average total time in bed
        val averageTotalTimeInBed = getAverageTotalTimeInBed(sleepLogs)

        // Calculate average bed time and wake-up time
        val averageBedTime = calculateAverageTime(sleepLogs.map { it.bedTime.toLocalTime() })
        val averageWakeUpTime = calculateAverageTime(sleepLogs.map { it.wakeUpTime.toLocalTime() })

        // Calculate morning feeling frequencies
        val morningFeelingFrequencies = getMorningFeelingFrequencies(sleepLogs)

        return SleepLogAverageDTO(
            startDate = startDate,
            endDate = endDate,
            averageTotalTimeInBed = averageTotalTimeInBed,
            averageBedTime = averageBedTime,
            averageWakeUpTime = averageWakeUpTime,
            morningFeelingFrequencies = morningFeelingFrequencies,
            totalDays = 30,
            daysWithLogs = sleepLogs.size
        )
    }

    private fun getMorningFeelingFrequencies(sleepLogs: List<SleepLog>): Map<MorningFeeling, Int> = sleepLogs
        .groupBy { it.morningFeeling }
        .mapValues { it.value.size }
        .let { frequencies ->
            // Ensure all feelings are represented in the map
            MorningFeeling.values().associateWith { feeling ->
                frequencies[feeling] ?: 0
            }
        }

    private fun getAverageTotalTimeInBed(sleepLogs: List<SleepLog>): Int = sleepLogs
        .map { it.totalTimeInBed }
        .average()
        .toInt()

    private fun calculateAverageTime(times: List<LocalTime>): LocalTime {
        if (times.isEmpty()) return LocalTime.MIDNIGHT

        // Convert times to minutes since midnight
        val minutesSinceMidnight = times.map {
            it.hour * 60 + it.minute
        }

        // Calculate average minutes
        val averageMinutes = minutesSinceMidnight.average().toInt()

        // Convert back to LocalTime
        return LocalTime.of(
            averageMinutes / 60,
            averageMinutes % 60
        )
    }
}