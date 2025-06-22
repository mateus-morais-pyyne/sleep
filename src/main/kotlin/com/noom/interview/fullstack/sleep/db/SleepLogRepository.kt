package com.noom.interview.fullstack.sleep.db

import com.noom.interview.fullstack.sleep.model.SleepLog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate
import java.util.*

interface SleepLogRepository : JpaRepository<SleepLog, UUID> {
    @Query("SELECT s FROM SleepLog s WHERE s.user.id = :userId ORDER BY s.dateOfSleep DESC LIMIT 1")
    fun findLatestByUserId(@Param("userId") userId: UUID): SleepLog?

    @Query("SELECT s FROM SleepLog s WHERE s.user.id = :userId AND s.dateOfSleep BETWEEN :startDate AND :endDate")
    fun findByUserIdAndDateRange(
        @Param("userId") userId: UUID,
        @Param("startDate") startDate: LocalDate,
        @Param("endDate") endDate: LocalDate
    ): List<SleepLog>
}