package ny.photomap.data

import ny.photomap.domain.TimeStamp
import timber.log.Timber
import java.lang.System
import javax.inject.Inject

class TimeStampImpl @Inject constructor() : TimeStamp {
    override val currentTime: Long
        get() = System.currentTimeMillis()

    override fun hasTimePassed(lastUpdateTime: Long, day: Int): Boolean {
        val targetDay = lastUpdateTime + (24 * 60 * 60 * 1_000 * day)
        Timber.d("lastUpdateTime: $lastUpdateTime, day : $day, targetDay : $targetDay")
        return currentTime >= targetDay
    }
}