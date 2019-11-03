package com.sac.dulanga.newsapp.comman

/**
 * Created by chamithdulanga on 2019-11-03.
 */
interface ApplicationConstants {
    companion object {
        const val NETWORK_ERROR = "No Internet Connection. Please try again later"

        const val DATA_SAVED_SUCCESS = "Your data has been successfully saved."

        const val INVALID_EMAIL = "Invalid Email address"

        const val PROFILE_PIC_NAME = "output_image.jpg"

        const val FILE_PROVIDER = "com.sac.dulanga.newsapp.provider"

        const val OPEN_CAMERA = "android.media.action.IMAGE_CAPTURE"

        const val OPEN_GALLERY = "android.intent.action.GET_CONTENT"

        const val PERMISSION_DENIED = "You denied the permission"

        const val FAILED_TO_GET_IMAGE = "Failed to get image"
    }
}