package mening.dasturim.myvoiceapp.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

object Constants {
    const val CHECK_CAMERA_PERMISSION=200
    const val CHECK_STORAGE_PERMISSION=400
    const val PICK_CAMER_PERMISSION=10
    const val PICK_GALLERY_PERMISSION=50
    const val RECOGNIZER_RESULT=5

    private val unAuthorized = MutableLiveData<Boolean>()

    fun setUnAuthorized(isUnAuthorized: Boolean) {
        unAuthorized.value = isUnAuthorized
    }

    fun getUnAuthorized(): LiveData<Boolean> {
        return unAuthorized
    }

}