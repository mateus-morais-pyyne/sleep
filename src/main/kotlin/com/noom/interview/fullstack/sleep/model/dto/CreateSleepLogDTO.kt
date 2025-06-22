package com.noom.interview.fullstack.sleep.model.dto

import java.time.LocalDateTime

data class CreateSleepLogDTO(
    val bedTime: LocalDateTime,
    val wakeUpTime: LocalDateTime,
    val morningFeeling: String
)