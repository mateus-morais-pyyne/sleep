package com.noom.interview.fullstack.sleep.model

import com.noom.interview.fullstack.sleep.model.enumeration.MorningFeeling
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "tb_sleep_logs")
class SleepLog(

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "UUID")
    val id: UUID? = null,

    @Column(name = "date_of_sleep", nullable = false)
    var dateOfSleep: LocalDate,

    @Column(name = "bed_time", nullable = false)
    var bedTime: LocalDateTime,

    @Column(name = "wake_up_time", nullable = false)
    var wakeUpTime: LocalDateTime,

    @Column(name = "total_time_in_bed", nullable = false)
    var totalTimeInBed: Int = 0,

    @Enumerated(EnumType.STRING)
    @Column(name = "morning_feeling", nullable = false)
    var morningFeeling: MorningFeeling,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User
) {
}