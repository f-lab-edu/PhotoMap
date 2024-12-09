package ny.photomap.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
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
    fun `모두_조회`() = runTest {
        dao.deleteAll()
        dao.insert(ENTITY_1)
        val list = dao.getAll()
        assert(list[0] == ENTITY_1)
    }

    @Test
    fun `장소_필터링_조회`() = runTest {
        dao.deleteAll()
        dao.insertAll(ENTITY_LIST)
        val list = dao.getLocationOf(
            latitude = LOCATION_2.first, longitude = LOCATION_2.second, range = 3.0
        )
        assertThat(list).hasSize(4)

        val list2 = dao.getLocationOf(
            latitude = LOCATION_3.first, longitude = LOCATION_3.second, range = 0.0
        )
        assertThat(list2).hasSize(0)
    }

    @Test
    fun `장소_시간_필터링_조회`() = runTest {
        dao.deleteAll()
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
        dao.deleteAll()
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
    fun `추가_단일`() = runTest {
        dao.deleteAll()
        dao.insert(ENTITY_1)
        assertThat(dao.getAll()).hasSize(1)
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


}