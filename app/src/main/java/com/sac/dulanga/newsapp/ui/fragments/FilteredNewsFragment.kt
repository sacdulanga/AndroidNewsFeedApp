package com.sac.dulanga.newsapp.ui.fragments

import android.annotation.SuppressLint
import android.content.res.ColorStateList
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
import com.google.android.material.chip.Chip
import com.sac.dulanga.newsapp.BaseApplication
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 * Created by chamithdulanga on 2019-11-02.
 */

class FilteredNewsFragment : Fragment() {

    internal val TAG = this@FilteredNewsFragment.javaClass.simpleName

    companion object {

        fun newInstance(): FilteredNewsFragment {
            return FilteredNewsFragment()
        }

        @SuppressLint("StaticFieldLeak")
        var filteredNewsFragment: FilteredNewsFragment? = null
    }

    private val client by lazy {
        ApiWorker.create()
    }

    private var disposable: Disposable? = null
    private var newsArticleAdapter: NewsArticleAdapter? = null

    @BindView(R.id.articles_recycler)
    lateinit  var articlesRecycler: RecyclerView

    @BindView(R.id.filter_chip)
    lateinit  var filterChip: Chip

    @BindView(R.id.toolbar)
    lateinit  var mToolBar: Toolbar

    @BindView(R.id.refresh_layout)
    lateinit var swipeRefreshLayout: SwipeRefreshLayout

    @BindView(R.id.empty_feed)
    lateinit var emptyMsg : ImageView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var rootView: View? = null
        try {
            rootView = inflater.inflate(R.layout.fragment_filtered_news, container, false)
            ButterKnife.bind(this, rootView!!)

            getActionBarToolbar(rootView)
            setUpToolBar()

            filteredNewsFragment = this;

            setupRecycler(arrayListOf<NewsArticle>())
            showArticles(getString(R.string.text_bitcoin))

            filterChip.text = "bitcoin"
            filterChip.chipIcon = ContextCompat.getDrawable(requireContext(), R.drawable.icon_filter_chip)
            filterChip.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.singup_bottom_color))

            swipeRefreshLayout.setOnRefreshListener{
                newsArticleAdapter!!.updateData(arrayListOf<NewsArticle>(), 1)
                showArticles(filterChip.text.toString())
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
    private fun showArticles(query: String) {

        if(CommonUtils.isConnectedToInternet(requireActivity())){
            CommonUtils.showLoading(requireActivity())
            disposable = client.getFilteredArticles(query, DomainConstants.API_AUTHORIZATION_KEY)
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
                                toggleView(true)
                                swipeRefreshLayout.isRefreshing = false
                                CommonUtils.showLongToast(requireActivity(), error.message.toString())
                                Log.e("ERROR", error.message)
                            }
                    )
        }else{
            CommonUtils.hideLoading()
            toggleView(true)
            swipeRefreshLayout.isRefreshing = false
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
        val mCustomView = layoutInflater.inflate(R.layout.custom_actionbar_filtered_news, null)
        val title = mCustomView.findViewById<View>(R.id.title) as TextView
        val btnFilter = mCustomView.findViewById<View>(R.id.btn_filter) as ImageView
        mToolBar!!.addView(mCustomView)
        title.text = getString(R.string.title_filtered_news)

        btnFilter.setOnClickListener {
            if(FilterBottomSheetFragment.filterBottomSheetFragment == null) {
                val bottomSheetFragment = FilterBottomSheetFragment()
                bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.getTag())
            }
        }

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

    fun setFilter(msg: String){
        filterChip.text = msg
        filterChip.chipIcon = ContextCompat.getDrawable(requireContext(), R.drawable.icon_filter_chip)
        filterChip.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.singup_bottom_color))
        showArticles(msg)
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
        filteredNewsFragment = null
        CommonUtils.hideLoading()
        super.onDestroyView()
    }
}