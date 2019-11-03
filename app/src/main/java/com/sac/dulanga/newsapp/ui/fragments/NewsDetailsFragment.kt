package com.sac.dulanga.newsapp.ui.fragments

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.VisibleForTesting
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.sac.dulanga.newsapp.R
import com.sac.dulanga.newsapp.dto.NewsArticle
import com.sac.dulanga.newsapp.ui.activities.MainActivity
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.assist.FailReason
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener

/**
 * Created by chamithdulanga on 2019-11-02.
 */

class NewsDetailsFragment: Fragment() {

    internal val TAG = this@NewsDetailsFragment.javaClass.simpleName

    companion object {
        private const val ARG_PARAM = "myObject"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param myObject as MyObject.
         * @return A new instance of fragment MyFragment.
         */
        fun newInstance(myObject: NewsArticle): NewsDetailsFragment {
            val fragment = NewsDetailsFragment()
            val args = Bundle()
            args.putParcelable(ARG_PARAM, myObject)
            fragment.arguments = args
            return fragment
        }

        var newsDetailsFragment: NewsDetailsFragment? = null
    }

    private lateinit var myObject: NewsArticle
    private var externalLink : String = ""

    @BindView(R.id.header_image)
    lateinit var mImageView: ImageView

    @BindView(R.id.image_progress)
    lateinit var mProgressBar: ProgressBar

    @BindView(R.id.toolbar)
    lateinit var mToolbar: Toolbar

    @BindView(R.id.collapsing_toolbar)
    lateinit var collapsingToolbarLayout: CollapsingToolbarLayout

    @BindView(R.id.author)
    lateinit var mAuthor: TextView

    @BindView(R.id.title)
    lateinit var mTitle: TextView

    @BindView(R.id.date)
    lateinit var mDate: TextView

    @BindView(R.id.description)
    lateinit var mDescription: TextView

    @BindView(R.id.content)
    lateinit var mContent: TextView

    @BindView(R.id.fab_btn)
    lateinit var fabBtn: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            myObject = arguments!!.getParcelable(ARG_PARAM)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        var rootView: View? = null
        try {
            rootView = inflater.inflate(R.layout.fragment_news_details, container, false)
            ButterKnife.bind(this, rootView!!)

            newsDetailsFragment = this

            getActionBarToolbar(rootView)
            setUpToolBar()

            createImageView(myObject.urlToImage, mImageView, mProgressBar)
            if(myObject.source.name != null)collapsingToolbarLayout.title = "Source : " + myObject.source.name
            if(myObject.author != null)mAuthor.text = "Author : " + myObject.author
            if(myObject.title != null) mTitle.text = myObject.title
            if(myObject.publishedAt != null) mDate.text = myObject.publishedAt.substringBefore("T")
            if(myObject.description != null)mDescription.text = myObject.description
            if(myObject.content != null && myObject.content.isNotEmpty())mContent.text = myObject.content
            else if(myObject.description != null && myObject.description.isNotEmpty())mContent.text = myObject.description

            if(myObject.url != null && myObject.url.isNotEmpty()){
                fabBtn.visibility = View.VISIBLE
                externalLink = myObject.url
            } else fabBtn.visibility = View.GONE

            Log.d(TAG, "onCreateView")
        } catch (e: Exception) {
            Log.e(TAG, "onCreateView: $e")
        }
        return rootView
    }


    private fun setProductImage(imageUrl: String?, productImage: ImageView, imageProgress: ProgressBar?) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            ImageLoader.getInstance().displayImage(imageUrl, productImage, object : SimpleImageLoadingListener() {
                override fun onLoadingStarted(imageUri: String?, view: View?) {
                    if (imageProgress != null) imageProgress.visibility = View.VISIBLE
                }

                override fun onLoadingFailed(imageUri: String?, view: View?, failReason: FailReason?) {
                    if (imageProgress != null) imageProgress.visibility = View.GONE
                }

                override fun onLoadingComplete(imageUri: String?, view: View?, loadedImage: Bitmap?) {
                    if (imageProgress != null) imageProgress.visibility = View.GONE
                }
            })
        } else {
            if (imageProgress != null) imageProgress.visibility = View.GONE
        }

    }

    private fun createImageView(imageURL: String?, productImageView: ImageView, imageProgress: ProgressBar?) {
        val callback = Handler.Callback {
            try {
                if (imageURL != null && !imageURL.isEmpty())
                    setProductImage(imageURL, productImageView, imageProgress)
                else if (imageProgress != null) imageProgress.visibility = View.GONE
            } catch (e: Exception) {
                e.printStackTrace()
            }

            false
        }
        Handler(callback).sendEmptyMessage(1)
    }

    private fun getActionBarToolbar(v: View): Toolbar? {
        (activity as MainActivity).setSupportActionBar(mToolbar)
        mToolbar!!.setContentInsetsAbsolute(0, 0)
        /** remove actionbar unnecessary left margin  */
        return mToolbar
    }


    private fun setUpToolBar() {
        mToolbar.navigationIcon = resources.getDrawable(R.drawable.ic_back_arrow)
        mToolbar.setNavigationOnClickListener(View.OnClickListener {
            fragmentManager?.popBackStack()
        })
    }

    @OnClick(R.id.fab_btn)
    fun onClickFabBtn() {
        val openURL = Intent(Intent.ACTION_VIEW)
        openURL.data = Uri.parse(externalLink)
        startActivity(openURL)
    }

    override fun onDestroyView() {
        newsDetailsFragment = null
        super.onDestroyView()
    }
}

