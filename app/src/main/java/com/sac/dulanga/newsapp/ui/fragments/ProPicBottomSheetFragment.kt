package com.sac.dulanga.newsapp.ui.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentUris
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import butterknife.BindView
import butterknife.ButterKnife
import com.sac.dulanga.newsapp.R
import com.sac.dulanga.newsapp.comman.ApplicationConstants
import com.sac.dulanga.newsapp.comman.CommonUtils
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.io.File

/**
 * Created by chamithdulanga on 2019-11-02.
 */

class ProPicBottomSheetFragment : BottomSheetDialogFragment() {

    internal val TAG = this@ProPicBottomSheetFragment.javaClass.simpleName

    companion object {

        fun newInstance(): FilteredNewsFragment {
            return FilteredNewsFragment()
        }

        @SuppressLint("StaticFieldLeak")
        var proPicBottomSheetFragment: ProPicBottomSheetFragment? = null
    }

    private val FINAL_TAKE_PHOTO = 1
    private val FINAL_CHOOSE_PHOTO = 2
    private var imageUri: Uri? = null

    @BindView(R.id.cameraLayout)
    lateinit var cameraLayout: LinearLayout

    @BindView(R.id.galleryLayout)
    lateinit var galleryLayout: LinearLayout

    @BindView(R.id.cancelLayout)
    lateinit var cancelLayout: LinearLayout

    @BindView(R.id.bottomSheet)
    lateinit var bottomSheet: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var rootView: View? = null
        try {
            rootView = inflater.inflate(R.layout.pro_pic_bottom_sheet, container, false)
            ButterKnife.bind(this, rootView!!)

            proPicBottomSheetFragment = this

            cameraLayout.setOnClickListener {
                val outputImage = File(requireActivity().externalCacheDir, ApplicationConstants.PROFILE_PIC_NAME)
                if (outputImage.exists()) {
                    outputImage.delete()
                }
                outputImage.createNewFile()
                imageUri = if (Build.VERSION.SDK_INT >= 24) {
                    FileProvider.getUriForFile(requireActivity(), ApplicationConstants.FILE_PROVIDER, outputImage)
                } else {
                    Uri.fromFile(outputImage)
                }


                val checkSelfPermission = ContextCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.CAMERA)
                val checkSelfPermissionW = ContextCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                if (checkSelfPermission != PackageManager.PERMISSION_GRANTED && checkSelfPermissionW != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
                } else {
                    val intent = Intent(ApplicationConstants.OPEN_CAMERA)
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                    startActivityForResult(intent, FINAL_TAKE_PHOTO)
                }

            }

            galleryLayout.setOnClickListener {
                val checkSelfPermission = ContextCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                if (checkSelfPermission != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
                } else {
                    openAlbum()
                }
            }
            cancelLayout!!.setOnClickListener { dismiss() }
            Log.d(TAG, "onCreateView")
        } catch (e: Exception) {
            Log.e(TAG, "onCreateView: $e")
        }
        return rootView
    }



    private fun openAlbum(){
        val intent = Intent(ApplicationConstants.OPEN_GALLERY)
        intent.type = "image/*"
        startActivityForResult(intent, FINAL_CHOOSE_PHOTO)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            1 ->
                if (grantResults.isNotEmpty() && grantResults.get(0) == PackageManager.PERMISSION_GRANTED){
                    openAlbum()
                }
                else {
                    CommonUtils.showTopSnackBar(ApplicationConstants.PERMISSION_DENIED, ContextCompat.getColor(requireContext(), R.color.red), requireActivity())
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            FINAL_TAKE_PHOTO ->
                if (resultCode == Activity.RESULT_OK) {
                    val bitmap = BitmapFactory.decodeStream(requireActivity().contentResolver.openInputStream(imageUri))

                    if(ProfileFragment.profileFragmentFragment != null)
                        ProfileFragment.profileFragmentFragment!!.setProfileImage(bitmap, imageUri.toString())

                    dismiss()
                }
            FINAL_CHOOSE_PHOTO ->
                if (resultCode == Activity.RESULT_OK) {
                        handleImageOnKitkat(data)
                    dismiss()
                }

        }
    }


    private fun handleImageOnKitkat(data: Intent?) {
        var imagePath: String? = null
        val uri = data!!.data
        if (DocumentsContract.isDocumentUri(requireActivity(), uri)){
            val docId = DocumentsContract.getDocumentId(uri)
            if ("com.android.providers.media.documents" == uri.authority){
                val id = docId.split(":")[1]
                val selsetion = MediaStore.Images.Media._ID + "=" + id
                imagePath = imagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selsetion)
            }
            else if ("com.android.providers.downloads.documents" == uri.authority){
                val contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(docId))
                imagePath = imagePath(contentUri, null)
            }
        }
        else if ("content".equals(uri.scheme, ignoreCase = true)){
            imagePath = imagePath(uri, null)
        }
        else if ("file".equals(uri.scheme, ignoreCase = true)){
            imagePath = uri.path
        }

        displayImage(imagePath)
    }

    private fun imagePath(uri: Uri?, selection: String?): String {
        var path: String? = null
        val cursor = requireActivity().contentResolver.query(uri, null, selection, null, null )
        if (cursor != null){
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
            }
            cursor.close()
        }
        return path!!
    }

    private fun displayImage(imagePath: String?){
        if (imagePath != null) {
            val bitmap = BitmapFactory.decodeFile(imagePath)

            if(ProfileFragment.profileFragmentFragment != null)
                ProfileFragment.profileFragmentFragment!!.setProfileImage(bitmap, imagePath)
        }
        else {
            CommonUtils.showTopSnackBar(ApplicationConstants.FAILED_TO_GET_IMAGE, ContextCompat.getColor(requireContext(), R.color.red), requireActivity())
        }
    }

    override fun onDestroyView() {
        proPicBottomSheetFragment = null
        super.onDestroyView()
    }
}
