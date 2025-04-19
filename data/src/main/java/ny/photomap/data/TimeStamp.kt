package ny.photomap.data

interface TimeStamp {
    val currentTime : Long

    /**
     * @param lastUpdateTime 과거 시간
     * @param day 기준 일 수
     * @return 현재 시간이 (과거 시간 + 기준 일 수)를 지나간 시점이면 true, 아직 현재 시간이 기준 일을 지나가지 못 했으면 false
     */
    fun hasTimePassed(lastUpdateTime: Long, day : Int) : Boolean

}