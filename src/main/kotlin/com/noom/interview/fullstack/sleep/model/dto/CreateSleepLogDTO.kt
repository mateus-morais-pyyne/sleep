package com.noom.interview.fullstack.sleep.model.dto

import com.noom.interview.fullstack.sleep.model.enumeration.MorningFeeling
import java.time.LocalDate
import java.time.LocalDateTime

data class CreateSleepLogDTO(
    val dateOfSleep: LocalDate,
    val bedTime: LocalDateTime,
    val wakeUpTime: LocalDateTime,
    val morningFeeling: MorningFeeling
)