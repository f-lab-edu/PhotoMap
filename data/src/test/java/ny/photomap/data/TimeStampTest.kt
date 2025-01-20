package ny.photomap.data

import ny.photomap.domain.TimeStamp
import org.junit.Before
import org.junit.Test

class TimeStampTest {

    private lateinit var timeStamp : TimeStamp

    @Before
    fun setUp() {
        timeStamp = TimeStampImpl()
    }

    @Test
    fun `현재 시간 반환`() {
        val startTime = System.currentTimeMillis()
        val testResult = timeStamp.currentTime
        val endTime = System.currentTimeMillis()
        assert(testResult in startTime..endTime)
    }

    @Test
    fun `현재의 시점이 저장된 과거의 시점으로부터 기준 일수를 지나갔을 경우 true 반환`() {
        val time3DaysAgo = getTimeMillisTimeAfterDay(-3)
        assert(timeStamp.hasTimePassed(time3DaysAgo, 2) == true)
    }

    @Test
    fun `현재의 시점이 저장된 과거의 시점으로부터 기준 일 수를 아직 지나가지 않았을 경우 false 반환`() {
        val time3DaysAgo = getTimeMillisTimeAfterDay(-3)
        assert(timeStamp.hasTimePassed(time3DaysAgo, 5) == false)
    }

    fun getTimeMillisTimeAfterDay(day: Int) : Long {
        return System.currentTimeMillis() + (24 * 60 * 60 * 1000 * day)
    }
}