package com.sac.dulanga.newsapp.ui.fragments

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.sac.dulanga.newsapp.R
import com.sac.dulanga.newsapp.comman.ApplicationConstants
import com.sac.dulanga.newsapp.comman.CommonUtils
import com.sac.dulanga.newsapp.dto.User
import com.sac.dulanga.newsapp.ui.activities.MainActivity
import de.hdodenhof.circleimageview.CircleImageView

/**
 * Created by chamithdulanga on 2019-11-02.
 */

class ProfileFragment: Fragment() {
    internal val TAG = this@ProfileFragment.javaClass.simpleName

    companion object {

        fun newInstance(): ProfileFragment {
            return ProfileFragment()
        }
        var profileFragmentFragment: ProfileFragment? = null
    }

    private var profileUrl : String = ""

    @BindView(R.id.profile_img)
    lateinit  var profilePic: CircleImageView

    @BindView(R.id.btn_edit_image)
    lateinit  var editProfilePic: Button

    @BindView(R.id.text_name)
    lateinit  var textName: EditText

    @BindView(R.id.text_email)
    lateinit  var textEmail: EditText

    @BindView(R.id.text_phone)
    lateinit  var textPhone: EditText

    @BindView(R.id.btn_save)
    lateinit  var btnSave: Button

    @BindView(R.id.toolbar)
    lateinit  var mToolBar: Toolbar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        var rootView: View? = null
        try {
            rootView = inflater.inflate(R.layout.fragment_profile, container, false)
            ButterKnife.bind(this, rootView!!)

            profileFragmentFragment = this
            getActionBarToolbar(rootView)
            setUpToolBar()

            var user = (requireActivity() as MainActivity).retrieveUserFromSharedPreferences()
            if(user.name.isNotEmpty()) textName.setText(user.name)
            if(user.email.isNotEmpty()) textEmail.setText(user.email)
            if(user.mobile.isNotEmpty()) textPhone.setText(user.mobile)
            if(user.image.isNotEmpty()){
                profileUrl = user.image
                if(profileUrl.contains(requireActivity().packageName)) {
                    val bitmap = BitmapFactory.decodeStream(requireActivity().contentResolver.openInputStream(Uri.parse(profileUrl)))
                    profilePic!!.setImageBitmap(bitmap)
                }
                else {
                    val bitmap = BitmapFactory.decodeFile(profileUrl)
                    profilePic!!.setImageBitmap(bitmap)
                }
            }

            if(profileUrl.isEmpty()) editProfilePic.visibility = View.GONE
            else editProfilePic.visibility = View.VISIBLE

            Log.d(TAG, "onCreateView")
        } catch (e: Exception) {
            Log.e(TAG, "onCreateView: $e")
        }
        return rootView
    }

    private fun getActionBarToolbar(v: View): Toolbar? {
        (activity as MainActivity).setSupportActionBar(mToolBar)
        mToolBar!!.setContentInsetsAbsolute(0, 0)
        /** remove actionbar unnecessary left margin  */
        return mToolBar
    }

    private fun setUpToolBar() {
        val mCustomView = layoutInflater.inflate(R.layout.custom_actionbar_home, null)
        val title = mCustomView.findViewById<View>(R.id.title) as TextView
        mToolBar!!.addView(mCustomView)
        title.text = getString(R.string.title_profile)
        val parent = mCustomView.parent as Toolbar
        parent.setPadding(0, 0, 0, 0)//for tab otherwise give space in tab
        parent.setContentInsetsAbsolute(0, 0)
    }

    fun setProfileImage(bitmap: Bitmap, path: String){
        profilePic!!.setImageBitmap(bitmap)
        profileUrl = path
        editProfilePic.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        profileFragmentFragment = null
        super.onDestroy()
    }

    @OnClick(R.id.btn_save)
    fun onClickSave() {
        var user = User()

        var isError : Boolean = false

        var textName : String = textName.text.toString().trim()
        var emailString: String = textEmail.text.toString().trim()
        var textMobile: String = textPhone.text.toString().trim()


         if(textName.isEmpty()){
            isError = true
            CommonUtils.showTopSnackBar(getString(R.string.error_msg_name), ContextCompat.getColor(requireContext(), R.color.red), requireActivity())
        }else if(emailString.isEmpty() || (emailString.isNotEmpty() && !CommonUtils.checkEmail(emailString))) {
            isError = true
            CommonUtils.showTopSnackBar(getString(R.string.error_msg_email), ContextCompat.getColor(requireContext(), R.color.red), requireActivity())
        }else if(textMobile.isEmpty() || textMobile.length != 12){
            isError = true
            CommonUtils.showTopSnackBar(getString(R.string.error_msg_mobile), ContextCompat.getColor(requireContext(), R.color.red), requireActivity())
        }

        if(!isError) {
            user.name = textName
            user.email = emailString
            user.mobile = textMobile
            user.image = profileUrl

            (requireActivity() as MainActivity).saveUserToSharedPreferences(user)

            CommonUtils.showTopSnackBar(ApplicationConstants.DATA_SAVED_SUCCESS, ContextCompat.getColor(requireContext(), R.color.green), requireActivity())

        }
    }

    @OnClick(R.id.profile_img)
    fun onClickProfile() {
        if(profileUrl.isEmpty() && ProPicBottomSheetFragment.proPicBottomSheetFragment == null) {
            val bottomSheetFragment = ProPicBottomSheetFragment()
            bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.getTag())
        }
    }

    @OnClick(R.id.btn_edit_image)
    fun onClickRemoveProfilePic() {
        profilePic.setImageResource(R.drawable.temp_pro_pic)
        profileUrl = ""
        editProfilePic.visibility = View.GONE
    }

}