package ny.photomap.data

import ny.photomap.data.db.PhotoLocationEntity


val LOCATION_1 = 37.579617 to 126.977041 // 경복궁
val LOCATION_2 = 37.2871202 to 127.0119379 // 수원 화성
val LOCATION_3 = 35.8011781 to 128.098098 // 해인사

const val TIME_20241209_090331 = 1733735011000L // 2024년 December 9일 Monday AM 9:03:31 (GMT)
const val TIME_20250128_135637 = 1738072597000L  // 2025년 January 28일 Tuesday PM 1:56:37 (GMT)
val URI_WITH_NUMBER = "URI #"

val ENTITY_1 = PhotoLocationEntity(
    uri = "URI 1",
    name = "경복궁",
    latitude = LOCATION_1.first,
    longitude = LOCATION_1.second,
    generatedTime = TIME_20241209_090331,
    addedTime = TIME_20241209_090331,
)

val ENTITY_2 = PhotoLocationEntity(
    uri = "URI 2",
    name = "수원 화성",
    latitude = LOCATION_2.first,
    longitude = LOCATION_2.second,
    generatedTime = TIME_20250128_135637,
    addedTime = TIME_20250128_135637,
)

val ENTITY_3 = PhotoLocationEntity(
    uri = "URI 3",
    name = "해인사",
    latitude = LOCATION_3.first,
    longitude = LOCATION_3.second,
    generatedTime = TIME_20241209_090331,
    addedTime = TIME_20241209_090331,
)

val ENTITY_LIST: List<PhotoLocationEntity>
    get() = (0..100).map {
        PhotoLocationEntity(
            uri = "$URI_WITH_NUMBER $it",
            name = "$it",
            latitude = LOCATION_2.first + it,
            longitude = LOCATION_2.second + it,
            generatedTime = TIME_20241209_090331 + (it * 10_000),
            addedTime = TIME_20241209_090331 + (it * 20_000),
        )
    }