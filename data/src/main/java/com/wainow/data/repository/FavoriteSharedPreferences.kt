package com.wainow.data.repository

import android.content.Context
import android.service.autofill.SaveCallback
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.wainow.domain.entities.CompanyProfile
import com.wainow.domain.entities.Stock.Companion.TAG
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import java.io.IOException
import java.lang.reflect.Type


class FavoriteSharedPreferences(
    context: Context
) {
    private val gson = Gson()
    private var favoriteList: ArrayList<String> = ArrayList()
    private val sharedPreferences = context.getSharedPreferences(
        SHARED_PREF_NAME,
        Context.MODE_PRIVATE
    )

    init {
        getFavorite()
    }

    companion object{
        const val SHARED_PREF_NAME = "SHARED_PREF_FAVORITE"
        val LIST_TYPE: Type = object : TypeToken<List<String?>?>() {}.type
    }

    fun getFavorite(): ArrayList<String> {
        return try {
            favoriteList = gson.fromJson(
                sharedPreferences.getString(SHARED_PREF_NAME, SHARED_PREF_NAME),
                LIST_TYPE
            )
            Log.d(TAG, "SharedPreferences: LOAD: $favoriteList")
            favoriteList
        } catch (e: Exception) {
            Log.d(TAG, "SharedPreferences: ${e.printStackTrace()}")
            ArrayList()
        }
    }

    fun addFavorite(company: String): Boolean{
        return try{
            getFavorite()
            favoriteList.add(company)
            saveFavorite(favoriteList)
            Log.d(TAG, "SharedPreferences: ADD ITEM: $favoriteList")
            true
        } catch (e: Exception) {
            Log.d(TAG, "SharedPreferences: ${e.printStackTrace()}")
            false
        }
    }

    fun deleteFavorite(company: String): Boolean{
        return try {
            getFavorite()
            Log.d(TAG, "SharedPreferences: REMOVE ITEM : $company")
            Log.d(TAG, "SharedPreferences: REMOVE : ${favoriteList.remove(company)}")
            saveFavorite(favoriteList)
            Log.d(TAG, "SharedPreferences: $favoriteList")
            true
        } catch (e: Exception){
            Log.d(TAG, "SharedPreferences: ${e.printStackTrace()}")
            false
        }
    }

    private fun saveFavorite(list: List<String>): Boolean {
        return try {
            sharedPreferences.edit().putString(SHARED_PREF_NAME, gson.toJson(list)).apply()
            Log.d(TAG, "SharedPreferences: SAVE: $list")
            true
        } catch (e: Exception){
            Log.d(TAG, "SharedPreferences: ${e.printStackTrace()}")
            false
        }
    }
}