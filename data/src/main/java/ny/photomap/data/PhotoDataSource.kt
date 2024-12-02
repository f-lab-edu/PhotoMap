package ny.photomap.data

import android.content.ContentResolver
import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore

class PhotoDataSource(private val contentResolver: ContentResolver) {

    private val projection = arrayOf(
        MediaStore.Images.Media._ID
    )

    fun getAllPhotoUriList(): List<Uri> = getPhotoUriList(null, null)
    fun getDateRangePhotoUriList(startMilliSecond: Long, endMilliSecond: Long): List<Uri> =
        getPhotoUriList(
            "${MediaStore.Images.Media.DATE_TAKEN} BETWEEN ? AND ?",
            arrayOf(startMilliSecond.toString(), endMilliSecond.toString())
        )

    private fun getPhotoUriList(selection: String?, selectionArgs: Array<String>?): List<Uri> {
        val photoUriList = mutableListOf<Uri>()
        try {
            val query: Cursor? = contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null,
                null
            )

            query?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val uri =
                        ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                    photoUriList.add(uri)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return photoUriList
    }

}