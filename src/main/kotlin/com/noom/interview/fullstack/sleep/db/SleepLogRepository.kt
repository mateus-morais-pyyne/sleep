package com.noom.interview.fullstack.sleep.db

import com.noom.interview.fullstack.sleep.model.SleepLog
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface SleepLogRepository : JpaRepository<SleepLog, UUID>