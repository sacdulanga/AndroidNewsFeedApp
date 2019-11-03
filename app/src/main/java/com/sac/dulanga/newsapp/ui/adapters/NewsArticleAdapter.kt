package com.sac.dulanga.newsapp.ui.adapters
import android.graphics.Bitmap
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.sac.dulanga.newsapp.R
import com.sac.dulanga.newsapp.dto.NewsArticle
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.assist.FailReason
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener
import kotlinx.android.synthetic.main.card_layout.view.*

/**
 * Created by chamithdulanga on 2019-11-02.
 */

class NewsArticleAdapter(
        private val articleList: MutableList<NewsArticle>,
        private val listener: (NewsArticle) -> Unit
    ): RecyclerView.Adapter<NewsArticleAdapter.ArticleHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ArticleHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.card_layout, parent, false))

    override fun onBindViewHolder(holder: ArticleHolder, position: Int) = holder.bind(articleList[position], listener)

    override fun getItemCount() = articleList.size

    class ArticleHolder(articleView: View): RecyclerView.ViewHolder(articleView) {

        fun bind(article: NewsArticle, listener: (NewsArticle) -> Unit) = with(itemView) {
            iv_notification_img.setImageResource(R.color.singup_bottom_color)

            title.text = article.title
            body.text = article.description
            date.text = article.publishedAt.substringBefore("T")
            if(article.urlToImage != null && article.urlToImage.isNotEmpty())createImageView(article.urlToImage, iv_notification_img, image_progress)
            else image_progress.visibility = View.GONE

            setOnClickListener { listener(article) }
        }

        private fun setProductImage(imageUrl: String?, productImage: ImageView, imageProgress: ProgressBar?) {
            if (imageUrl != null && imageUrl.isNotEmpty()) {
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
                    if (imageURL != null && imageURL.isNotEmpty())
                        setProductImage(imageURL, productImageView, imageProgress)
                    else if (imageProgress != null) imageProgress.visibility = View.GONE
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                false
            }
            Handler(callback).sendEmptyMessage(1)
        }
    }

    fun updateData(messageList: List<NewsArticle>, flag: Int) {
        if (flag == 0) { //append
            for (i in messageList.indices) {
                articleList!!.add(messageList[i])
                notifyItemInserted(itemCount)
            }
            //notifyDataSetChanged();
        } else { //clear all
            articleList!!.clear()
            notifyDataSetChanged()
        }
    }


}
