package com.noom.interview.fullstack.sleep.service

import com.noom.interview.fullstack.sleep.db.SleepLogRepository
import com.noom.interview.fullstack.sleep.model.SleepLog
import com.noom.interview.fullstack.sleep.model.dto.CreateSleepLogDTO
import com.noom.interview.fullstack.sleep.model.enumeration.MorningFeeling
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.time.temporal.ChronoUnit
import java.util.*

@Service
class SleepLogService(
    private val sleepLogRepository: SleepLogRepository,
    private val userService: UserService
) {
    
    @Transactional
    fun createSleepLog(createSleepLogDTO: CreateSleepLogDTO, userId: UUID): SleepLog {
        val user = userService.findById(userId)
        
        val totalTimeInBed = ChronoUnit.MINUTES.between(
            createSleepLogDTO.bedTime,
            createSleepLogDTO.wakeUpTime
        ).toInt()
        
        if (totalTimeInBed < 0) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Wake up time must be after bed time"
            )
        }
        
        val sleepLog = SleepLog(
            dateOfSleep = createSleepLogDTO.bedTime.toLocalDate(),
            bedTime = createSleepLogDTO.bedTime,
            wakeUpTime = createSleepLogDTO.wakeUpTime,
            totalTimeInBed = totalTimeInBed,
            morningFeeling = MorningFeeling.valueOf(createSleepLogDTO.morningFeeling.uppercase()),
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
}