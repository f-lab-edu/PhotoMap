package ny.photomap.data

import android.content.ContentResolver
import android.database.MatrixCursor
import android.provider.MediaStore
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.eq
import org.mockito.kotlin.isNull
import org.mockito.kotlin.mock
import org.mockito.kotlin.same
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PhotoRepositoryTest {

    private lateinit var contentResolver: ContentResolver
    private lateinit var dataSource: PhotoDataSource
    private lateinit var repository: PhotoRepository

    @Before
    fun setUp() {
        contentResolver = mock()
        dataSource = Mockito.spy<PhotoDataSource>(PhotoDataSource(contentResolver))
        repository = Mockito.spy<PhotoRepository>(
            PhotoRepository(
                contentResolver = contentResolver,
                dataSource = dataSource
            )
        )
    }

    @Test
    fun `모든 사진 정보 리스트 조회`() {
        val cursor = MatrixCursor(IMAGE_COLUMNS).apply {
            arrayOf(
                IMAGE_1,
                IMAGE_2,
                IMAGE_3_TAKEN_DATE_NULL,
                IMAGE_4_TAKEN_DATE_NULL,
                IMAGE_5
            ).forEach {
                addRow(it)
            }
        }

        whenever(
            contentResolver.query(
                same(MediaStore.Images.Media.EXTERNAL_CONTENT_URI),
                anyOrNull(),
                isNull(),
                isNull(),
                anyOrNull(),
                isNull(),
            )
        ).thenReturn(cursor)

        whenever(repository.convertPhotoInfo(getMockUri(IMAGE_1_ID))).thenReturn(
            PHOTOINFO_1
        )
        whenever(repository.convertPhotoInfo(getMockUri(IMAGE_2_ID))).thenReturn(
            PHOTOINFO_2
        )
        whenever(repository.convertPhotoInfo(getMockUri(IMAGE_3_ID))).thenReturn(
            PHOTOINFO_3
        )
        whenever(repository.convertPhotoInfo(getMockUri(IMAGE_4_ID))).thenReturn(
            null
        )
        whenever(repository.convertPhotoInfo(getMockUri(IMAGE_5_ID))).thenReturn(
            null
        )

        val list = repository.getAllPhotoInfoList()

        assert(list.size == 3)
    }

    @Test
    fun `특정 날짜 사진 정보 리스트 조회_값 있음`() {
        val cursor = MatrixCursor(IMAGE_COLUMNS).apply {
            arrayOf(
                IMAGE_1,
                IMAGE_2
            ).forEach {
                addRow(it)
            }
        }

        whenever(
            contentResolver.query(
                same(MediaStore.Images.Media.EXTERNAL_CONTENT_URI),
                anyOrNull(),
                eq("${MediaStore.Images.Media.DATE_TAKEN} BETWEEN ? AND ?"),
                eq(
                    arrayOf(
                        TIME_2010_01_01_12_00_00.toString(),
                        TIME_2024_01_01_12_00_00.toString()
                    )
                ),
                anyOrNull(),
                isNull(),
            )
        ).thenReturn(cursor)

        whenever(repository.convertPhotoInfo(getMockUri(IMAGE_1_ID))).thenReturn(
            PHOTOINFO_1
        )
        whenever(repository.convertPhotoInfo(getMockUri(IMAGE_2_ID))).thenReturn(
            PHOTOINFO_2
        )

        val list =
            repository.getDateRangePhotoInfoList(TIME_2010_01_01_12_00_00, TIME_2024_01_01_12_00_00)

        assert(list.size == 2)
    }

    @Test
    fun `특정 날짜 사진 정보 리스트 조회_값 없음`() {
        val cursor = MatrixCursor(IMAGE_COLUMNS)

        whenever(
            contentResolver.query(
                same(MediaStore.Images.Media.EXTERNAL_CONTENT_URI),
                anyOrNull(),
                eq("${MediaStore.Images.Media.DATE_TAKEN} BETWEEN ? AND ?"),
                eq(arrayOf(0.toString(), TIME_2010_01_01_12_00_00.toString())),
                anyOrNull(),
                anyOrNull(),
            )
        ).thenReturn(cursor)

        val list = repository.getDateRangePhotoInfoList(0, TIME_2010_01_01_12_00_00)
        assert(list.isEmpty())
    }

    @Test
    fun `특정 위치 사진 정보 리스트 조회_값 있음`() {
        val cursor = MatrixCursor(IMAGE_COLUMNS).apply {
            arrayOf(
                IMAGE_1,
                IMAGE_2,
                IMAGE_3_TAKEN_DATE_NULL,
                IMAGE_4_TAKEN_DATE_NULL,
                IMAGE_5
            ).forEach {
                addRow(it)
            }
        }

        whenever(
            contentResolver.query(
                same(MediaStore.Images.Media.EXTERNAL_CONTENT_URI),
                anyOrNull(),
                isNull(),
                isNull(),
                anyOrNull(),
                isNull(),
            )
        ).thenReturn(cursor)

        val surroundRange = 0.000001

        whenever(
            repository.convertPhotoInfo(
                uri = getMockUri(IMAGE_1_ID),
                targetLatitude = LOCATION_2.first,
                targetLongitude = LOCATION_2.second,
                surroundRange = surroundRange
            )
        ).thenReturn(
            null
        )
        whenever(
            repository.convertPhotoInfo(
                uri = getMockUri(IMAGE_2_ID),
                targetLatitude = LOCATION_2.first,
                targetLongitude = LOCATION_2.second,
                surroundRange = surroundRange
            )
        ).thenReturn(
            null
        )
        whenever(
            repository.convertPhotoInfo(
                uri = getMockUri(IMAGE_3_ID),
                targetLatitude = LOCATION_2.first,
                targetLongitude = LOCATION_2.second,
                surroundRange = surroundRange
            )
        ).thenReturn(
            PHOTOINFO_3
        )
        whenever(
            repository.convertPhotoInfo(
                uri = getMockUri(IMAGE_4_ID),
                targetLatitude = LOCATION_2.first,
                targetLongitude = LOCATION_2.second,
                surroundRange = surroundRange
            )
        ).thenReturn(
            null
        )
        whenever(
            repository.convertPhotoInfo(
                uri = getMockUri(IMAGE_5_ID),
                targetLatitude = LOCATION_2.first,
                targetLongitude = LOCATION_2.second,
                surroundRange = surroundRange
            )
        ).thenReturn(
            PHOTOINFO_5
        )

        val list = repository.getLocationPhotoInfoList(
            targetLatitude = LOCATION_2.first,
            targetLongitude = LOCATION_2.second,
            surroundRange = surroundRange
        )

        println("list.size: ${list.size}")

        assert(list.size == 2)
    }

    @Test
    fun `특정 위치 사진 정보 리스트 조회_값 없음`() {
        val cursor = MatrixCursor(IMAGE_COLUMNS).apply {
            arrayOf(
                IMAGE_1,
                IMAGE_2,
                IMAGE_3_TAKEN_DATE_NULL,
                IMAGE_4_TAKEN_DATE_NULL,
                IMAGE_5
            ).forEach {
                addRow(it)
            }
        }

        whenever(
            contentResolver.query(
                same(MediaStore.Images.Media.EXTERNAL_CONTENT_URI),
                isNull(),
                isNull(),
                isNull(),
                anyOrNull(),
                isNull(),
            )
        ).thenReturn(cursor)

        val surroundRange = 0.000001

        whenever(
            repository.convertPhotoInfo(
                uri = getMockUri(IMAGE_1_ID),
                targetLatitude = LOCATION_3.first,
                targetLongitude = LOCATION_3.second,
                surroundRange = surroundRange
            )
        ).thenReturn(
            null
        )
        whenever(
            repository.convertPhotoInfo(
                uri = getMockUri(IMAGE_2_ID),
                targetLatitude = LOCATION_3.first,
                targetLongitude = LOCATION_3.second,
                surroundRange = surroundRange
            )
        ).thenReturn(
            null
        )
        whenever(
            repository.convertPhotoInfo(
                uri = getMockUri(IMAGE_3_ID),
                targetLatitude = LOCATION_3.first,
                targetLongitude = LOCATION_3.second,
                surroundRange = surroundRange
            )
        ).thenReturn(
            null
        )
        whenever(
            repository.convertPhotoInfo(
                uri = getMockUri(IMAGE_4_ID),
                targetLatitude = LOCATION_3.first,
                targetLongitude = LOCATION_3.second,
                surroundRange = surroundRange
            )
        ).thenReturn(
            null
        )
        whenever(
            repository.convertPhotoInfo(
                uri = getMockUri(IMAGE_5_ID),
                targetLatitude = LOCATION_3.first,
                targetLongitude = LOCATION_3.second,
                surroundRange = surroundRange
            )
        ).thenReturn(
            null
        )

        val list = repository.getLocationPhotoInfoList(
            targetLatitude = LOCATION_3.first,
            targetLongitude = LOCATION_3.second,
            surroundRange = 0.0
        )

        assert(list.isEmpty())
    }
}