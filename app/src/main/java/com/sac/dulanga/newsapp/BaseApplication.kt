package com.sac.dulanga.newsapp

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import com.nostra13.universalimageloader.core.assist.QueueProcessingType


/**
 * Created by chamithdulanga on 2019-11-02.
 */

class BaseApplication : Application() {

    private val TAG = this@BaseApplication.javaClass.getSimpleName()

    private var preferences: SharedPreferences? = null

    var loadDetailScreen : Boolean = false

    override fun onCreate() {
        super.onCreate()
        baseApplication = applicationContext as BaseApplication
        preferences = getSharedPreferences(packageName, Context.MODE_PRIVATE)
        initImageLoader(applicationContext)
    }

    private fun initImageLoader(context: Context) {
        val options = DisplayImageOptions.Builder()
                .cacheInMemory(false)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build()

        val config = ImageLoaderConfiguration.Builder(context)
        config.threadPriority(Thread.NORM_PRIORITY - 2)
        config.denyCacheImageMultipleSizesInMemory()
        config.diskCacheFileNameGenerator(Md5FileNameGenerator())
        config.diskCacheSize(50 * 1024 * 1024) // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO)
        config.defaultDisplayImageOptions(options)

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build())
    }


    companion object {
        var baseApplication: BaseApplication? = null
            private set
    }


}