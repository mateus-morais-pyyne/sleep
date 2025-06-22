package com.noom.interview.fullstack.sleep.model.dto

import com.noom.interview.fullstack.sleep.model.SleepLog
import com.noom.interview.fullstack.sleep.model.enumeration.MorningFeeling
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

data class SleepLogDTO(
    val id: UUID?,
    val userId: UUID?,
    val dateOfSleep: LocalDate,
    val bedTime: LocalDateTime,
    val wakeUpTime: LocalDateTime,
    val totalTimeInBed: Int,
    val morningFeeling: MorningFeeling
) {
    constructor(sleepLog: SleepLog) : this(
        id = sleepLog.id,
        userId = sleepLog.user.id,
        dateOfSleep = sleepLog.dateOfSleep,
        bedTime = sleepLog.bedTime,
        wakeUpTime = sleepLog.wakeUpTime,
        totalTimeInBed = sleepLog.totalTimeInBed,
        morningFeeling = sleepLog.morningFeeling
    )
}