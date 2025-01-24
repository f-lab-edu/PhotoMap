package ny.photomap.data

import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import ny.photomap.domain.TimeStamp
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit

class TimeStampTest {

    private lateinit var timeStamp: TimeStamp

    @Before
    fun setUp() {
        timeStamp = TimeStampImpl()
    }

    @Test
    fun `현재 시간 반환`() {
        val startTime = System.currentTimeMillis()
        val testResult = timeStamp.currentTime
        val endTime = System.currentTimeMillis()
        assertTrue(
            "[$startTime]과 [$endTime] 사이에 호출한 timeStamp.currentTime 값($testResult)이 시간 범위를 넘어갔습니다.",
            testResult in startTime..endTime,
        )
    }

    @Test
    fun `현재의 시점이 저장된 과거의 시점으로부터 기준 일수를 지나갔을 경우 true 반환`() {
        val time3DaysAgo = getTimeMillisTimeAfterDay(-3)
        assertTrue(
            "현재 시간이 (3일 전의 시간 기록 + 2일)을 지난 시점이라 true를 반환해야 함에도 false를 반환했습니다.",
            timeStamp.hasTimePassed(time3DaysAgo, 2)
        )
    }

    @Test
    fun `현재의 시점이 저장된 과거의 시점으로부터 기준 일 수를 아직 지나가지 않았을 경우 false 반환`() {
        val time3DaysAgo = getTimeMillisTimeAfterDay(-3)
        assertFalse(
            "현재 시간이 (3일 전의 시간 기록 + 5일)을 지나지 않은 시점이라 false를 반환해야 하지만 true를 반환했습니다.",
            timeStamp.hasTimePassed(time3DaysAgo, 5)
        )
    }

    fun getTimeMillisTimeAfterDay(day: Int): Long {
        return System.currentTimeMillis() + TimeUnit.DAYS.toMillis(day.toLong())
    }
}