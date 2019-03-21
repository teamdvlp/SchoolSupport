package com.teamttdvlp.goodthanbefore.schoolsupport.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.teamttdvlp.goodthanbefore.schoolsupport.support.getViewModel
import com.teamttdvlp.goodthanbefore.schoolsupport.viewmodel.EditProfileViewModel
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_edit_profile.*
import android.content.Intent
import android.R
import android.app.Activity
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import com.teamttdvlp.goodthanbefore.schoolsupport.model.CurrentUser
import com.teamttdvlp.goodthanbefore.schoolsupport.model.users.User
import com.teamttdvlp.goodthanbefore.schoolsupport.support.dataclass.UpdateInfoEvent
import com.teamttdvlp.goodthanbefore.schoolsupport.support.dataclass.UploadAvatarEvent
import java.io.ByteArrayOutputStream


class EditProfileActivity : AppCompatActivity(), UpdateInfoEvent {

    lateinit var mViewModel : EditProfileViewModel

    lateinit var currentUser : User

    var currentAvatar : Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.teamttdvlp.goodthanbefore.schoolsupport.R.layout.activity_edit_profile)
        currentUser = intent.extras!!.getSerializable("User") as User
        currentAvatar = CurrentUser.bitmapUserAvatar
        img_avatar.setImageBitmap(currentAvatar)
        mViewModel = getViewModel()
        addControls()
        addEvents ()
    }

    private fun addControls() {

    }

    private fun addEvents() {
        btn_close.setOnClickListener {
            finish()
        }

        btn_save.setOnClickListener {
            save()
        }

        btn_edit_profile_picture.setOnClickListener {
            CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this)
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                val resultUri = result.uri
                img_avatar.setImageURI(resultUri)
                currentAvatar = (img_avatar.drawable as BitmapDrawable).bitmap
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
                Log.e("ERROR: ", "EditProfileActivity.kt \n onActivityResult \n $error")
            }
        }
    }

    // Update User info
    override fun onUpdateSuccess() {
        finish()
        Log.e("Update:", "Success")
    }

    override fun onUpdateFailed() {
        Log.e("Update:", "Failed")
    }

    private fun save () {
        mViewModel.uploadAvatar(currentUser, currentAvatar!!, object : UploadAvatarEvent {
            override fun onUploadSuccess(downloadUri: String) {
                Log.e("Upload:", "Success. Uri: $downloadUri" )
                currentUser.DisplayName = edt_user_name.text.toString()
                currentUser.About = edt_about.text.toString()
                currentUser.Avatar = downloadUri
                CurrentUser.bitmapUserAvatar = currentAvatar
                mViewModel.updateUserInfo(currentUser, this@EditProfileActivity)
            }

            override fun onUploadFailed(message : String) {
                Log.e("Upload:", "Failed. Error: $message")
            }
        })
    }

}