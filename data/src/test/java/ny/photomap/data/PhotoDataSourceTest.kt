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

        whenever(contentResolver.query(
            same(MediaStore.Images.Media.EXTERNAL_CONTENT_URI),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
        )).thenReturn(cursor)


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

        whenever(contentResolver.query(
            same(MediaStore.Images.Media.EXTERNAL_CONTENT_URI),
            anyOrNull(),
            eq("${MediaStore.Images.Media.DATE_TAKEN} BETWEEN ? AND ?"),
            eq(arrayOf(100.toString(), 2000.toString())),
            anyOrNull(),
            anyOrNull(),
        )).thenReturn(cursor)


        val list = PhotoDataSource(contentResolver).getDateRangePhotoUriList(100, 2000)
        assert(list.size == 2)
    }

    @Test
    fun `특정 기간의 이미지 URI 조회_값 없음`() {

        val cursor = MatrixCursor(IMAGE_COLUMNS)

        whenever(contentResolver.query(
            same(MediaStore.Images.Media.EXTERNAL_CONTENT_URI),
            anyOrNull(),
            eq("${MediaStore.Images.Media.DATE_TAKEN} BETWEEN ? AND ?"),
            eq(arrayOf(0.toString(), 100.toString())),
            anyOrNull(),
            anyOrNull(),
        )).thenReturn(cursor)

        val list = PhotoDataSource(contentResolver).getDateRangePhotoUriList(0, 100)
        assert(list.isEmpty())
    }

    companion object MockData {
        private val IMAGE_COLUMNS = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATE_TAKEN,
            MediaStore.Images.Media.DATA,
        )

        private val IMAGE_1_ID = 1L
        private val IMAGE_2_ID = 2L
        private val IMAGE_3_ID = 3L
        private val IMAGE_4_ID = 4L
        private val IMAGE_5_ID = 5L

        private val IMAGE_1 = arrayOf(
            IMAGE_1_ID,
            1000,
            "PHOTO URI 1",
        )

        private val IMAGE_2 = arrayOf(
            IMAGE_2_ID,
            2000,
            "PHOTO URI 2",
        )

        private val IMAGE_3_TAKEN_DATE_NULL = arrayOf(
            IMAGE_3_ID,
            null,
            "PHOTO URI 3"
        )

        private val IMAGE_4_TAKEN_DATE_NULL = arrayOf(
            IMAGE_4_ID,
            null,
            "PHOTO URI 4",
        )
        private val IMAGE_5 = arrayOf(
            IMAGE_5_ID,
            2001,
            "PHOTO URI 5",
        )
    }
}