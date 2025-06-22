package com.noom.interview.fullstack.sleep.db

import com.noom.interview.fullstack.sleep.model.SleepLog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface SleepLogRepository : JpaRepository<SleepLog, UUID> {
    @Query("SELECT s FROM SleepLog s WHERE s.user.id = :userId ORDER BY s.dateOfSleep DESC LIMIT 1")
    fun findLatestByUserId(@Param("userId") userId: UUID): SleepLog?
}