package com.noom.interview.fullstack.sleep.commons

import com.noom.interview.fullstack.sleep.model.dto.CreateSleepLogDTO
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import java.time.temporal.ChronoUnit

object Util {
    fun getTotalTimeInBed(createSleepLogDTO: CreateSleepLogDTO): Int {
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
        return totalTimeInBed
    }
}