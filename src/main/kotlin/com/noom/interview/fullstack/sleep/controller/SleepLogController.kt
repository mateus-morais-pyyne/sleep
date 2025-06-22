package com.noom.interview.fullstack.sleep.controller

import com.noom.interview.fullstack.sleep.model.dto.CreateSleepLogDTO
import com.noom.interview.fullstack.sleep.model.dto.SleepLogDTO
import com.noom.interview.fullstack.sleep.service.SleepLogService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.HttpClientErrorException
import java.util.*

@RestController
@RequestMapping("/api/users/{userId}/sleep-logs")
class SleepLogController(
    private val sleepLogService: SleepLogService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    fun createSleepLog(
        @PathVariable userId: UUID,
        @RequestBody createSleepLogDTO: CreateSleepLogDTO
    ): SleepLogDTO {
        val createdSleepLog = sleepLogService.createSleepLog(createSleepLogDTO, userId)
        return SleepLogDTO(createdSleepLog)
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    fun getSleepLogById(
        @PathVariable userId: UUID,
        @PathVariable id: UUID
    ): SleepLogDTO {
        val sleepLog = sleepLogService.findById(id)
        
        if (sleepLog.user.id != userId) {
            throw HttpClientErrorException(HttpStatus.NOT_FOUND)
        }
        
        return SleepLogDTO(sleepLog)
    }

    @GetMapping("/latest")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    fun getLatestSleepLog(
        @PathVariable userId: UUID
    ): SleepLogDTO {
        val latestSleepLog = sleepLogService.findLatestByUserId(userId)
        return SleepLogDTO(latestSleepLog)
    }
}