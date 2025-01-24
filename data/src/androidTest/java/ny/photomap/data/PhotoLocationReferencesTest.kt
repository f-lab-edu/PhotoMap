package ny.photomap.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import ny.photomap.data.preferences.PhotoLocationPreferencesImpl
import ny.photomap.data.preferences.PhotoLocationReferences
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import kotlin.coroutines.CoroutineContext

@OptIn(ExperimentalCoroutinesApi::class)
class PhotoLocationReferencesTest : CoroutineScope {

    private lateinit var testDataStore: DataStore<Preferences>
    private lateinit var photoLocationReferences: PhotoLocationReferences
    private lateinit var testDispatcher: TestDispatcher

    @get:Rule
    val temporaryFolder = TemporaryFolder.builder().assureDeletion().build()

    override val coroutineContext: CoroutineContext
        get() = testDispatcher + Job()

    @Before
    fun setUp() {
        println("setUp")
        testDispatcher = UnconfinedTestDispatcher()


        testDataStore =
            PreferenceDataStoreFactory.create(
                scope = this,
                produceFile = { temporaryFolder.newFile("photo_location.preferences_pb") }
            )

        photoLocationReferences = PhotoLocationPreferencesImpl(testDataStore)
    }

    @Test
    fun `저장된_시간_정보가_없을_경우_0L_반환`() = runTest {
        val syncTime = photoLocationReferences.timeSyncDatabaseFlow.first()
        assertEquals(0L, syncTime)
    }

    @Test
    fun `저장된_시간_정보를_반환`() = runTest {
        val time = System.currentTimeMillis()
        photoLocationReferences.updateTimeSyncDatabase(time)
        val syncTime = photoLocationReferences.timeSyncDatabaseFlow.first()
        assertEquals(time, syncTime)
    }

}