package ny.photomap.data

import android.content.ContentUris
import android.net.Uri
import android.provider.MediaStore
import ny.photomap.model.PhotoInfo

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

const val TIME_2023_01_01_12_00_00 = 1672542000000 // 2023.1.1 12:00:00
const val TIME_2024_01_01_12_00_00 = 1704078000000L // 2024.1.1 12:00:00
const val TIME_2024_01_01_12_01_00 = 1262314860000L // 2024.1.1 12:01:00
const val TIME_2010_01_01_12_00_00 = 1262314800000L // 2010.1.1 12:00:00

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

private fun getMockUri(id: Long): Uri =
    ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

val PHOTOINFO_1 = PhotoInfo(
    uri = getMockUri(IMAGE_1_ID),
    latitude = 300.0,
    longitude = 200.0,
    generationTime = IMAGE_1[1].toString()
)