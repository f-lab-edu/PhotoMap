package ny.photomap.data

import ny.photomap.data.db.PhotoLocationEntity


val LOCATION_1 = 37.579617 to 126.977041 // 경복궁
val LOCATION_2 = 37.2871202 to 127.0119379 // 수원 화성
val LOCATION_3 = 35.8011781 to 128.098098 // 해인사

const val TIME_1 = 1733735011L // 2024년 December 9일 Monday AM 9:03:31 (GMT)
val URI_WITH_NUMBER = "URI #"

val ENTITY_1 = PhotoLocationEntity(
    uri = "URI 1",
    name = "1",
    latitude = LOCATION_1.first,
    longitude = LOCATION_1.second,
    generatedTime = TIME_1,
    addedTime = TIME_1,
)

val ENTITY_LIST: List<PhotoLocationEntity>
    get() = (0..100).map {
        PhotoLocationEntity(
            uri = "$URI_WITH_NUMBER $it",
            name = "$it",
            latitude = LOCATION_2.first + it,
            longitude = LOCATION_2.second + it,
            generatedTime = TIME_1 + (it * 10_000),
            addedTime = TIME_1 + (it * 20_000),
        )
    }