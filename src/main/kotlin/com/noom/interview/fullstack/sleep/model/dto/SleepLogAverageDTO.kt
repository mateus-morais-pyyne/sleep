package com.noom.interview.fullstack.sleep.model.dto

import com.noom.interview.fullstack.sleep.model.enumeration.MorningFeeling
import java.time.LocalDate
import java.time.LocalTime

data class SleepLogAverageDTO(
    val startDate: LocalDate,
    val endDate: LocalDate,

    val averageTotalTimeInBed: Int,
    val averageBedTime: LocalTime?,
    val averageWakeUpTime: LocalTime?,

    val morningFeelingFrequencies: Map<MorningFeeling, Int>?,

    val totalDays: Int,
    val daysWithLogs: Int
) {
}