package com.sac.dulanga.newsapp.ui.activities

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.sac.dulanga.newsapp.R
import com.sac.dulanga.newsapp.comman.IPreferencesKeys
import com.sac.dulanga.newsapp.dto.User
import com.sac.dulanga.newsapp.ui.fragments.FilteredNewsFragment
import com.sac.dulanga.newsapp.ui.fragments.NewsFeedFragment
import com.sac.dulanga.newsapp.ui.fragments.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.sac.dulanga.newsapp.comman.CommonUtils
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Created by chamithdulanga on 2019-11-02.
 */

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        replaceFragment(NewsFeedFragment())
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item->
        when(item.itemId){
            R.id.navigation_news_feed -> {
                println("home pressed")
                replaceFragment(NewsFeedFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_filtered_news -> {
                println("map pressed")
                replaceFragment(FilteredNewsFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_profile -> {
                println("cart pressed")
                replaceFragment(ProfileFragment())
                return@OnNavigationItemSelectedListener true
            }
        }

        false

    }


    private fun replaceFragment(fragment: Fragment){
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, fragment)
        fragmentTransaction.commit()
    }

    fun addFragment(fragment: Fragment?, TAG: String) {
        if (fragment == null) return
        val fragTransaction = supportFragmentManager.beginTransaction()
        fragTransaction.setCustomAnimations(R.anim.slide_in_left, 0, 0, R.anim.slide_out_left)
        fragTransaction.add(R.id.fragmentContainer, fragment, TAG)
        fragTransaction.addToBackStack(TAG)
        fragTransaction.commitAllowingStateLoss()
    }

    fun saveUserToSharedPreferences(user: User) {
        val preferences = this.getSharedPreferences(this.packageName, Context.MODE_PRIVATE)
        val prefsEditor = preferences.edit()
        val gson = Gson()
        val json = gson.toJson(user)
        prefsEditor.putString(IPreferencesKeys.USER_DETAILS, json)
        prefsEditor.apply()
    }

    /**
     * retrieve User data from shared preferences
     * @return
     */
     fun retrieveUserFromSharedPreferences(): User {
        val preferences = this.getSharedPreferences(this.packageName, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = preferences.getString(IPreferencesKeys.USER_DETAILS, "{}")
        return gson.fromJson<User>(json, User::class.java)
    }

    override fun onDestroy() {

        super.onDestroy()
    }

    override fun onPause() {
        CommonUtils.hideLoading()
        super.onPause()
    }
}
