import ny.photomap.domain.model.PhotoLocationModel

const val LOCATION_1_LATITUDE = 37.579617 // 경복궁
const val LOCATION_1_LONGITUDE = 126.977041 // 경복궁
val LOCATION_2_LATITUDE = 37.2871202 // 수원 화성
val LOCATION_2_LONGITUDE = 127.0119379 // 수원 화성
val LOCATION_3_LATITUDE = 35.8011781 // 해인사
val LOCATION_3_LONGITUDE = 128.098098 // 해인사

const val TIME_20241209_090331 = 1733735011000L // 2024년 December 9일 Monday AM 9:03:31 (GMT)
const val TIME_20250128_135637 = 1738072597000L  // 2025년 January 28일 Tuesday PM 1:56:37 (GMT)


val MODEL_1 = PhotoLocationModel(
    uri = "URI 1",
    name = "경복궁",
    latitude = LOCATION_1_LATITUDE,
    longitude = LOCATION_1_LONGITUDE,
    generatedTimeMillis = TIME_20241209_090331,
    addedTimeMillis = TIME_20241209_090331,
)

val MODEL_2 = PhotoLocationModel(
    uri = "URI 2",
    name = "수원 화성",
    latitude = LOCATION_2_LATITUDE,
    longitude = LOCATION_2_LONGITUDE,
    generatedTimeMillis = TIME_20250128_135637,
    addedTimeMillis = TIME_20250128_135637,
)

val MODEL_3 = PhotoLocationModel(
    uri = "URI 3",
    name = "해인사",
    latitude = LOCATION_3_LATITUDE,
    longitude = LOCATION_3_LONGITUDE,
    generatedTimeMillis = TIME_20241209_090331,
    addedTimeMillis = TIME_20241209_090331,
)

val mockModelList = listOf(MODEL_1, MODEL_2, MODEL_3)
