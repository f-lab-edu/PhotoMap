package ny.photomap.permission

import android.annotation.TargetApi
import android.os.Build
import androidx.annotation.StringRes
import ny.photomap.R

interface Permission {
    val permission: List<String>
    val titleRes: Int
    val needDescriptionRes: Int
}

@TargetApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
interface PermissionVisualUserSelected : Permission {
    val visualUserSelectedDescriptionRes: Int

    val mainPermission: String
    val visualUserSelectedPermission: String
        get() = android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
}


object ReadImagePermission : PermissionVisualUserSelected {
    override val mainPermission: String =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) android.Manifest.permission.READ_MEDIA_IMAGES
        else ""

    override val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
        listOf(
            mainPermission,
            visualUserSelectedPermission
        )
    else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        listOf(mainPermission)
    else listOf()

    @StringRes
    override val titleRes = R.string.permission_read_image_title

    @StringRes
    override val needDescriptionRes = R.string.permission_read_image_description

    @StringRes
    override val visualUserSelectedDescriptionRes: Int =
        R.string.permission_read_image_visual_user_selected_description

}



