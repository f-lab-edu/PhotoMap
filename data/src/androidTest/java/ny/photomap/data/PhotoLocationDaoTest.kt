package ny.photomap.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import ny.photomap.data.db.PhotoLocationDao
import ny.photomap.data.db.PhotoLocationDatabase
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidTest
class PhotoLocationDaoTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var database: PhotoLocationDatabase
    private lateinit var dao: PhotoLocationDao

    @Before
    fun setUp() {
        hiltRule.inject()
        dao = database.photoLocationDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `단일_삽입_후_모든_위치사진_조회`() = runTest {
        dao.insert(ENTITY_1)
        val result = dao.getAll()

        assertEquals(ENTITY_1, result[0])
        assertEquals(ENTITY_1.uri, result[0].uri)
        assertEquals(ENTITY_1.latitude, result[0].latitude)
        assertEquals(ENTITY_1.longitude, result[0].longitude)
    }

    @Test
    fun `리스트_삽입_후_모든_위치사진_조회`() = runTest {
        dao.insertAll(ENTITY_LIST)
        val result = dao.getAll()
        assertThat(result).hasSize(ENTITY_LIST.size)
    }


    @Test
    fun `중심_위치정보에서_반경_범위로_위치사진_조회_결과있음`() = runTest {
        dao.insertAll(ENTITY_LIST)

        val result = dao.getLocationOf(
            latitude = LOCATION_2.first, longitude = LOCATION_2.second, range = 3.0
        )

        assertThat(result).hasSize(4)
    }

    @Test
    fun `중심_위치정보에서_반경_범위로_위치사진_조회_결과없음`() = runTest {
        dao.insertAll(ENTITY_LIST)

        val result = dao.getLocationOf(
            latitude = LOCATION_3.first, longitude = LOCATION_3.second, range = 0.0
        )

        assertThat(result).hasSize(0)
    }

    @Test
    fun `화면_위아래_옆_범위로_위치사진_조회_결과_있음`() = runTest {
        val list = listOf(ENTITY_1, ENTITY_2, ENTITY_3)
        dao.insertAll(list)

        val result = dao.getLocationOf(
            northLatitude = 36.0,
            southLatitude = 35.0,
            eastLongitude = 130.0,
            westLongitude = 126.0,
        )

        assertThat(result).hasSize(1)
        assertEquals(result.first().name, "해인사")
    }

    @Test
    fun `화면_위아래_옆_범위로_위치사진_조회_결과_없음`() = runTest {
        val list = listOf(ENTITY_1, ENTITY_2, ENTITY_3)
        dao.insertAll(list)

        val result = dao.getLocationOf(
            northLatitude = 40.0,
            southLatitude = 38.0,
            eastLongitude = 130.0,
            westLongitude = 126.0,
        )

        assertThat(result).isEmpty()
    }

    @Test
    fun `중심_위치정보에서_반경_범위와_기록_날짜_범위로_위치사진_조회_범위필터링_테스트`() = runTest {
        dao.insertAll(ENTITY_LIST)

        // range 범위 확인
        val result = dao.getLocationAndDateOf(
            latitude = LOCATION_2.first,
            longitude = LOCATION_2.second,
            range = 3.0,
            fromTime = TIME_20241209_090331,
            toTime = TIME_20241209_090331 * 2
        )

        assertThat(result).hasSize(4)
    }

    @Test
    fun `중심_위치정보에서_반경_범위와_기록_날짜_범위로_위치사진_조회_위치필터링_테스트`() = runTest {
        dao.insertAll(ENTITY_LIST)

        // location 확인
        val result = dao.getLocationAndDateOf(
            latitude = LOCATION_1.first,
            longitude = LOCATION_1.second,
            range = 0.0,
            fromTime = TIME_20241209_090331,
            toTime = TIME_20241209_090331 * 2
        )

        assertThat(result).isEmpty()
    }

    @Test
    fun `중심_위치정보에서_반경_범위와_기록_날짜_범위로_위치사진_조회_시간필터링_테스트`() = runTest {
        dao.insertAll(ENTITY_LIST)

        // time 확인
        val result = dao.getLocationAndDateOf(
            latitude = LOCATION_2.first,
            longitude = LOCATION_2.second,
            range = 100.0,
            fromTime = TIME_20241209_090331,
            toTime = TIME_20241209_090331
        )
        assertThat(result).hasSize(1)
    }

    @Test
    fun `화면_위아래_옆_범위와_기록_날짜_범위로_위치사진_조회_위치필터링_테스트`() = runTest {
        val list = listOf(ENTITY_1, ENTITY_2, ENTITY_3)
        dao.insertAll(list)

        val result = dao.getLocationAndDateOf(
            northLatitude = 36.0,
            southLatitude = 35.0,
            eastLongitude = 130.0,
            westLongitude = 126.0,
            fromTime = TIME_20241209_090331 + TimeUnit.DAYS.toMillis(-3L),
            toTime = TIME_20241209_090331 + TimeUnit.DAYS.toMillis(3L),
        )

        assertThat(result).hasSize(1)
        assertEquals(result.first().name, "해인사")
    }

    @Test
    fun `화면_위아래_옆_범위와_기록_날짜_범위로_위치사진_조회_시간필터링_테스트`() = runTest {
        val list = listOf(ENTITY_1, ENTITY_2, ENTITY_3)
        dao.insertAll(list)

        val result = dao.getLocationAndDateOf(
            northLatitude = 42.0,
            southLatitude = 34.0,
            eastLongitude = 132.0,
            westLongitude = 124.0,
            fromTime = TIME_20250128_135637,
            toTime = TIME_20250128_135637,
        )

        assertThat(result).hasSize(1)
        assertEquals(result.first().name, "수원 화성")
    }

    @Test
    fun `중심_위치정보에서_반경_범위와_기록_날짜_범위로_위치사진_개수_조회_범위필러팅_테스트`() = runTest {
        dao.insertAll(ENTITY_LIST)

        // range 범위 확인
        val count = dao.getCountOfLocationAndDate(
            latitude = LOCATION_2.first,
            longitude = LOCATION_2.second,
            range = 3.0,
            fromTime = TIME_20241209_090331,
            toTime = TIME_20241209_090331 * 2
        )

        assertEquals(count, 4)
    }

    @Test
    fun `중심_위치정보에서_반경_범위와_기록_날짜_범위로_위치사진_개수_조회_장소필러팅_테스트`() = runTest {
        dao.insertAll(ENTITY_LIST)

        val count = dao.getCountOfLocationAndDate(
            latitude = LOCATION_1.first,
            longitude = LOCATION_1.second,
            range = 0.0,
            fromTime = TIME_20241209_090331,
            toTime = TIME_20241209_090331 * 2
        )
        assertEquals(count, 0)
    }

    @Test
    fun `중심_위치정보에서_반경_범위와_기록_날짜_범위로_위치사진_개수_조회_시간필러팅_테스트`() = runTest {
        dao.insertAll(ENTITY_LIST)

        val count = dao.getCountOfLocationAndDate(
            latitude = LOCATION_2.first,
            longitude = LOCATION_2.second,
            range = 100.0,
            fromTime = TIME_20241209_090331,
            toTime = TIME_20241209_090331
        )
        assertEquals(count, 1)
    }

    @Test
    fun `모두_삭제`() = runTest {
        dao.insertAll(ENTITY_LIST)

        dao.deleteAll()

        assertThat(dao.getAll()).isEmpty()
    }

    @Test
    fun `기존_정보_제거_후_업데이트`() = runTest {
        dao.insertAll(ENTITY_LIST)

        val list = listOf(ENTITY_1, ENTITY_2, ENTITY_3)
        dao.initialize(list)

        val result = dao.getAll()
        assertThat(result).contains(ENTITY_1)
        assertThat(result).contains(ENTITY_2)
        assertThat(result).contains(ENTITY_3)
        assertThat(result).hasSize(3)
    }

    @Test
    fun `가장_최근_위치사진_조회_파일생성날짜_순서`() = runTest {
        val photoLocationNow = ENTITY_1.copy(
            generatedTime = System.currentTimeMillis(),
            addedTime = System.currentTimeMillis()
        )
        dao.insertAll(ENTITY_LIST)
        dao.insert(photoLocationNow)

        val result = dao.getLatest()

        assertThat(result).isNotNull()
        assertEquals(photoLocationNow, result!!)
    }

    @Test
    fun `가장_최근_위치사진_조회_파일추가날짜_순서`() = runTest {
        val photoLocationNow = ENTITY_1.copy(
            generatedTime = TIME_20241209_090331,
            addedTime = System.currentTimeMillis()
        )
        dao.insertAll(ENTITY_LIST.map {
            it.copy(
                generatedTime = TIME_20241209_090331,
                addedTime = TIME_20241209_090331
            )
        })
        dao.insert(photoLocationNow)

        val result = dao.getLatest()

        assertThat(result).isNotNull()
        assertEquals(photoLocationNow, result!!)
    }


}