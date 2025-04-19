package ny.photomap.model

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

data class TimeUIModel(
    val takenTime: Long,
    val addedTime: Long,
)

fun TimeUIModel.takenTimeFormatString(): String? =
    convertUtcToLocalTimeString(this.takenTime, pattern)

fun TimeUIModel.addedTimeFormatString(): String? =
    convertUtcToLocalTimeString(this.addedTime, pattern)

const val pattern = "yyyy년 MM월 dd일 HH시 mm분 ss초"

fun convertUtcToLocalTimeString(utcMillis: Long, pattern: String): String? {
    return if (utcMillis == 0L) {
        null
    } else {
        val utcInstant = Instant.ofEpochMilli(utcMillis)

        val zoneId = ZoneId.systemDefault()

        val localDateTime = utcInstant.atZone(zoneId).toLocalDateTime()

        val formatter = DateTimeFormatter.ofPattern(pattern, Locale.getDefault())
        localDateTime.format(formatter)
    }

}