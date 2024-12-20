package ny.photomap.domain

interface TimeStamp {
    val currentTime : Long

    fun hasTimePassed(lastUpdateTime: Long, day : Int) : Boolean

}