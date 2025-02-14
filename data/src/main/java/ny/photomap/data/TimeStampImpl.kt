package ny.photomap.data

import timber.log.Timber
import java.lang.System
import javax.inject.Inject
import kotlin.time.Duration.Companion.days

class TimeStampImpl @Inject constructor() : TimeStamp {
    override val currentTime: Long
        get() = System.currentTimeMillis()

    override fun hasTimePassed(lastUpdateTime: Long, day: Int): Boolean {
        val targetDay = lastUpdateTime + day.days.inWholeMilliseconds
        Timber.d("lastUpdateTime: $lastUpdateTime, day : $day, targetDay : $targetDay")
        return currentTime >= targetDay
    }
}