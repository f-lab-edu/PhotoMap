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
        val list = dao.getAll()

        assertEquals(ENTITY_1, list[0])
        assertEquals(ENTITY_1.uri, list[0].uri)
        assertEquals(ENTITY_1.latitude, list[0].latitude)
        assertEquals(ENTITY_1.longitude, list[0].longitude)
    }

    @Test
    fun `리스트_삽입_후_모든_위치사진_조회`() = runTest {
        dao.insertAll(ENTITY_LIST)
        val list = dao.getAll()
        assertEquals(list.size, ENTITY_LIST.size)
    }


    @Test
    fun `중심_위치정보에서_반경_범위로_위치사진_조회`() = runTest {
        dao.insertAll(ENTITY_LIST)

        val list = dao.getLocationOf(
            latitude = LOCATION_2.first, longitude = LOCATION_2.second, range = 3.0
        )
        val list2 = dao.getLocationOf(
            latitude = LOCATION_3.first, longitude = LOCATION_3.second, range = 0.0
        )

        assertThat(list).hasSize(4)
        assertThat(list2).hasSize(0)
    }

    @Test
    fun `화면_위아래_옆_범위로_위치사진_조회`() = runTest {

    }

    @Test
    fun `중심_위치정보에서_반경_범위와_기록_날짜_범위로_위치사진_조회`() = runTest {

    }

    @Test
    fun `화면_위아래_옆_범위와_기록_날짜_범위로_위치사진_조회`() = runTest {

    }

    @Test
    fun `장소_시간_필터링_조회`() = runTest {
        dao.insertAll(ENTITY_LIST)

        // range 범위 확인
        val list = dao.getLocationAndDateOf(
            latitude = LOCATION_2.first,
            longitude = LOCATION_2.second,
            range = 3.0,
            fromTime = TIME_1,
            toTime = TIME_1 * 2
        )
        assertThat(list).hasSize(4)

        // location 확인
        val list2 = dao.getLocationAndDateOf(
            latitude = LOCATION_1.first,
            longitude = LOCATION_1.second,
            range = 0.0,
            fromTime = TIME_1,
            toTime = TIME_1 * 2
        )
        assertThat(list2).isEmpty()

        // time 확인
        val list3 = dao.getLocationAndDateOf(
            latitude = LOCATION_2.first,
            longitude = LOCATION_2.second,
            range = 100.0,
            fromTime = TIME_1,
            toTime = TIME_1
        )
        assertThat(list3).hasSize(1)
    }

    @Test
    fun `장소_시간_필터링_개수`() = runTest {
        dao.insertAll(ENTITY_LIST)

        // range 범위 확인
        val count = dao.getCountOfLocationAndDate(
            latitude = LOCATION_2.first,
            longitude = LOCATION_2.second,
            range = 3.0,
            fromTime = TIME_1,
            toTime = TIME_1 * 2
        )
        assert(count == 4)

        // location 확인
        val count2 = dao.getCountOfLocationAndDate(
            latitude = LOCATION_1.first,
            longitude = LOCATION_1.second,
            range = 0.0,
            fromTime = TIME_1,
            toTime = TIME_1 * 2
        )
        assert(count2 == 0)

        // time 확인
        val count3 = dao.getCountOfLocationAndDate(
            latitude = LOCATION_2.first,
            longitude = LOCATION_2.second,
            range = 100.0,
            fromTime = TIME_1,
            toTime = TIME_1
        )
        assert(count3 == 1)
    }

    @Test
    fun `추가_리스트`() = runTest {
        dao.deleteAll()
        dao.insertAll(ENTITY_LIST)
        assertThat(ENTITY_LIST).hasSize(ENTITY_LIST.size)
    }

    @Test
    fun `모두_삭제`() = runTest {
        dao.insert(ENTITY_1)
        dao.deleteAll()
        assertThat(dao.getAll()).isEmpty()
        dao.insertAll(ENTITY_LIST)
        dao.deleteAll()
        assertThat(dao.getAll()).isEmpty()
    }

    @Test
    fun `기존_정보_제거_후_업데이트`() = runTest {

    }

    @Test
    fun `가장_최근_위치사진_조회`() = runTest {

    }


}