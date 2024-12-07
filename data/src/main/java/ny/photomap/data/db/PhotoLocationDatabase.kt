package ny.photomap.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [PhotoLocationEntity::class], version = 1)
abstract class PhotoLocationDatabase : RoomDatabase() {
    abstract fun photoLocationDao(): PhotoLocationDao

    companion object {
        fun getInstance(context: Context): PhotoLocationDatabase = Room.databaseBuilder(
            context,
            PhotoLocationDatabase::class.java,
            "photo_location.db"
        )
            .build()
    }
}