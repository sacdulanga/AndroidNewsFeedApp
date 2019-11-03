package com.sac.dulanga.newsapp.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import butterknife.BindView
import butterknife.ButterKnife
import com.sac.dulanga.newsapp.R
import com.sac.dulanga.newsapp.comman.ApplicationConstants
import com.sac.dulanga.newsapp.comman.CommonUtils
import com.sac.dulanga.newsapp.comman.DomainConstants
import com.sac.dulanga.newsapp.dto.NewsArticle
import com.sac.dulanga.newsapp.model.rest.ApiWorker
import com.sac.dulanga.newsapp.ui.activities.MainActivity
import com.sac.dulanga.newsapp.ui.adapters.NewsArticleAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 * Created by chamithdulanga on 2019-11-02.
 */

class NewsFeedFragment: Fragment() {

    internal val TAG = this@NewsFeedFragment.javaClass.simpleName

    private val client by lazy {
        ApiWorker.create()
    }

    var disposable: Disposable? = null
    var newsArticleAdapter: NewsArticleAdapter? = null
    var isPullToRefresh: Boolean = false

    @BindView(R.id.articles_recycler)
    lateinit var articlesRecycler: RecyclerView

    @BindView(R.id.toolbar)
    lateinit var mToolBar: Toolbar

    @BindView(R.id.refresh_layout)
    lateinit var swipeRefreshLayout: SwipeRefreshLayout

    @BindView(R.id.empty_feed)
    lateinit var emptyMsg : ImageView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var rootView: View? = null
        try {
            rootView = inflater.inflate(R.layout.fragment_news_feed, container, false)
            ButterKnife.bind(this, rootView!!)

            getActionBarToolbar(rootView)
            setUpToolBar()

            setupRecycler(arrayListOf<NewsArticle>())
            showArticles()

            swipeRefreshLayout.setOnRefreshListener {
                newsArticleAdapter!!.updateData(arrayListOf<NewsArticle>(), 1)
                showArticles()
            }

            //** Set the colors of the Pull To Refresh View
            swipeRefreshLayout.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(requireActivity(), R.color.colorPrimary))
            swipeRefreshLayout.setColorSchemeColors(Color.WHITE)

            Log.d(TAG, "onCreateView")
        } catch (e: Exception) {
            Log.e(TAG, "onCreateView: $e")
        }

        return rootView
    }


    /* get list of articles */
    private fun showArticles() {

        if (CommonUtils.isConnectedToInternet(requireActivity())) {
            CommonUtils.showLoading(requireActivity())
            disposable = client.getMyArticles("us", DomainConstants.API_AUTHORIZATION_KEY)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            { result ->
                                CommonUtils.hideLoading()
                                swipeRefreshLayout.isRefreshing = false
                                newsArticleAdapter!!.updateData(arrayListOf<NewsArticle>(), 1)
                                newsArticleAdapter!!.updateData(result.articles, 0)
                                if(result?.articles != null && result.articles.size > 0){
                                    toggleView(false)
                                }
                            },
                            { error ->
                                CommonUtils.hideLoading()
                                swipeRefreshLayout.isRefreshing = false
                                CommonUtils.showLongToast(requireActivity(), error.message.toString())
                                toggleView(true)
                                Log.e("ERROR", error.message)
                            }
                    )
        } else {
            swipeRefreshLayout.isRefreshing = false
            toggleView(true)
            CommonUtils.hideLoading()
            CommonUtils.showTopSnackBar(ApplicationConstants.NETWORK_ERROR, ContextCompat.getColor(requireContext(), R.color.red), requireActivity())
        }


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
        title.text = getString(R.string.title_news_feed)
        val parent = mCustomView.parent as Toolbar
        parent.setPadding(0, 0, 0, 0)//for tab otherwise give space in tab
        parent.setContentInsetsAbsolute(0, 0)
    }


    private fun setupRecycler(articleList: MutableList<NewsArticle>) {
        articlesRecycler.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(requireActivity())
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        articlesRecycler.layoutManager = layoutManager

        newsArticleAdapter = NewsArticleAdapter(articleList) {
            if(NewsDetailsFragment.newsDetailsFragment == null) {
                (requireActivity() as MainActivity).addFragment(NewsDetailsFragment.newInstance(it), "NewsDetailsFragment")
            }
        }

        articlesRecycler.adapter = newsArticleAdapter
    }

    private fun toggleView(isFeedEmpty: Boolean) {
        if (isFeedEmpty) {
            emptyMsg.visibility = View.VISIBLE
            articlesRecycler.visibility = View.GONE
        } else {
            emptyMsg.visibility = View.GONE
            articlesRecycler.visibility = View.VISIBLE
        }
    }

    override fun onPause() {
        super.onPause()
        disposable?.dispose()
    }

    override fun onDestroyView() {
        CommonUtils.hideLoading()
        super.onDestroyView()
    }
}
