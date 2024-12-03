package ny.photomap.data

import android.content.ContentResolver
import android.database.MatrixCursor
import android.provider.MediaStore
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.same
import org.mockito.kotlin.whenever

import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PhotoDataSourceTest {

    private lateinit var contentResolver: ContentResolver

    @Before
    fun setUp() {
        contentResolver = mock()
    }

    @Test
    fun `모든 이미지 URI 조회`() {
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
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
            )
        ).thenReturn(cursor)


        val list = PhotoDataSource(contentResolver).getAllPhotoUriList()

        assert(list.size == 5)
    }

    @Test
    fun `특정 기간의 이미지 URI 조회_값 있음`() {
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
                eq(arrayOf(TIME_2010_01_01_12_00_00.toString(), TIME_2024_01_01_12_00_00.toString())),
                anyOrNull(),
                anyOrNull(),
            )
        ).thenReturn(cursor)


        val list = PhotoDataSource(contentResolver).getDateRangePhotoUriList(TIME_2010_01_01_12_00_00, TIME_2024_01_01_12_00_00)
        assert(list.size == 2)
    }

    @Test
    fun `특정 기간의 이미지 URI 조회_값 없음`() {

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

        val list = PhotoDataSource(contentResolver).getDateRangePhotoUriList(0, TIME_2010_01_01_12_00_00)
        assert(list.isEmpty())
    }
}