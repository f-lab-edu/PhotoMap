package ny.photomap.data

import android.content.ContentUris
import android.net.Uri
import android.provider.MediaStore
import ny.photomap.model.PhotoLocationData

val IMAGE_COLUMNS = arrayOf(
    MediaStore.Images.Media._ID,
    MediaStore.Images.Media.DATE_TAKEN,
    MediaStore.Images.Media.DATA,
)

const val IMAGE_1_ID = 1L
const val IMAGE_2_ID = 2L
const val IMAGE_3_ID = 3L
const val IMAGE_4_ID = 4L
const val IMAGE_5_ID = 5L

const val TIME_2023_01_01_12_00_00 = 1672542000000 // 2023:01:01 12:00:00
const val TIME_2024_01_01_12_00_00 = 1704078000000L // 2024:01:01 12:00:00
const val TIME_2024_01_01_12_01_00 = 1262314860000L // 2024:01:01 12:01:00
const val TIME_2010_01_01_12_00_00 = 1262314800000L // 2010:01:01 12:00:00

val LOCATION_1 = 37.579617 to 126.977041 // 경복궁
val LOCATION_2 = 37.2871202 to 127.0119379 // 수원 화성
val LOCATION_3 = 35.8011781 to 128.098098 // 해인사

val IMAGE_1 = arrayOf(
    IMAGE_1_ID,
    TIME_2023_01_01_12_00_00,
    "PHOTO URI 1",
)

val IMAGE_2 = arrayOf(
    IMAGE_2_ID,
    TIME_2024_01_01_12_00_00,
    "PHOTO URI 2",
)

val IMAGE_3_TAKEN_DATE_NULL = arrayOf(
    IMAGE_3_ID,
    null,
    "PHOTO URI 3"
)

val IMAGE_4_TAKEN_DATE_NULL = arrayOf(
    IMAGE_4_ID,
    null,
    "PHOTO URI 4",
)
val IMAGE_5 = arrayOf(
    IMAGE_5_ID,
    TIME_2024_01_01_12_01_00,
    "PHOTO URI 5",
)

fun getMockUri(id: Long): Uri =
    ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

// todo : PhotoDataSource 완성 후 작업
/*
val PHOTOINFO_1 = PhotoLocationData(
    uri = getMockUri(IMAGE_1_ID),
    latitude = LOCATION_1.first,
    longitude = LOCATION_1.second,
    generationTime = "2023:01:01 12:00:00"
)

val PHOTOINFO_2 = PhotoLocationData(
    uri = getMockUri(IMAGE_2_ID),
    latitude = LOCATION_1.first,
    longitude = LOCATION_1.second,
    generationTime = "2024:01:01 12:00:00"
)

val PHOTOINFO_3 = PhotoLocationData(
    uri = getMockUri(IMAGE_3_ID),
    latitude = LOCATION_2.first,
    longitude = LOCATION_2.second,
    generationTime = null
)

val PHOTOINFO_4 = PhotoLocationData(
    uri = getMockUri(IMAGE_4_ID),
    latitude = LOCATION_1.first,
    longitude = LOCATION_1.second,
    generationTime = null
)

val PHOTOINFO_5 = PhotoLocationData(
    uri = getMockUri(IMAGE_5_ID),
    latitude = LOCATION_2.first,
    longitude = LOCATION_2.second,
    generationTime = "2024:01:01 12:01:00"
)*/
