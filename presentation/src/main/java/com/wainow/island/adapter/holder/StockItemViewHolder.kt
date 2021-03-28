package com.wainow.island.adapter.holder

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wainow.data.repository.FavoriteSharedPreferences
import com.wainow.domain.entities.CompanyProfile
import com.wainow.domain.entities.Stock.Companion.TAG
import com.wainow.island.CompanyActivity
import com.wainow.island.CompanyActivity.Companion.NAME_SYMBOL
import com.wainow.island.CompanyActivity.Companion.NAME_VALUE
import com.wainow.island.R
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*


class StockItemViewHolder(
    view: View,
    private val changeFavoriteCallback: (Int, CompanyProfile) -> Unit,
) : RecyclerView.ViewHolder(view) {
    private val imageView: ImageView = view.findViewById(R.id.company_iv)
    private val starButton: ImageButton = view.findViewById(R.id.star_ib)
    private val tickerView: TextView = view.findViewById(R.id.ticker_tv)
    private val companyView: TextView = view.findViewById(R.id.company_tv)
    private val priceView: TextView = view.findViewById(R.id.price_tv)
    private val lastChangeView: TextView = view.findViewById(R.id.last_change_tv)
    private val sharedPreferences: FavoriteSharedPreferences = FavoriteSharedPreferences(itemView.context)

    @SuppressLint("SetTextI18n", "ResourceAsColor")
    fun bindTo(
        item: CompanyProfile,
        index: Int,
        isFavoriteList: Boolean
    ){
        Log.d(TAG, "ItemViewHolder: item: $item, isFavorite: $isFavoriteList")
        val stockPrice = round(item.price)
        val stockChange = with(item) { price - open }
        if (stockChange < 0) lastChangeView.setTextColor(Color.parseColor(COLOR_DEGREASE_PRICE))
        else lastChangeView.setTextColor(Color.parseColor(COLOR_INCREASE_PRICE))
        tickerView.text = item.ticker
        companyView.text = item.name
        priceView.text = "$$stockPrice"
        lastChangeView.text = with(item) { percent(stockChange, open) }
        setStar(if (item.isFavorite) R.drawable.ic_star else R.drawable.ic_empty_star)
        Glide
            .with(itemView.context)
            .load(item.logo)
            .error(R.drawable.ic_happy)
            .into(imageView)
        /*
        * Long click for deleting stock from favorite
         */
        itemView.setOnLongClickListener {
            changeFavorite(item, index, isFavoriteList)
            return@setOnLongClickListener true
        }
        /*
        * Click on the star: add/delete to/from favorite
         */
        starButton.setOnClickListener{
            changeFavorite(item, index, isFavoriteList)
        }
        /*
        * Click on the stock view for open CompanyActivity
         */
        itemView.setOnClickListener {
            val intent = Intent(itemView.context, CompanyActivity::class.java)
            intent.putExtra(NAME_SYMBOL, item.ticker)
            intent.putExtra(NAME_VALUE, stockPrice)
            itemView.context.startActivity(intent)
        }
    }

    companion object{
        fun create(
            parent: ViewGroup,
            changeFavoriteCallback: (Int, CompanyProfile) -> Unit,
        ): StockItemViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.stock_item, parent, false)
            return StockItemViewHolder(view, changeFavoriteCallback)
        }
        /*
        * Function which round stock's value
         */
        fun round(number: Double): Double {
            val df = DecimalFormat("#.###")
            df.roundingMode = RoundingMode.FLOOR
            return df.format(number).toDouble()
        }
        const val COLOR_INCREASE_PRICE = "#66fff8"
        const val COLOR_DEGREASE_PRICE = "#E08558"
    }
    /*
    * Function which print correct percent value
     */
    private fun percent(part: Double, total: Double): String {
        val c: String = if(part >= 0) "+" else "-"
        return "$c$${kotlin.math.abs(round(part))} ($c${kotlin.math.abs(round(part / total))}%)"
    }

    private fun setStar(drawable: Int){
        Glide
            .with(itemView.context)
            .load(drawable)
            .error(R.drawable.ic_empty_star)
            .into(starButton)
    }

    private fun changeFavorite(item: CompanyProfile, index: Int, isFavoriteList: Boolean){
        checkItem(item, isFavoriteList)
        changeFavoriteCallback(index, item)
    }
    /*
    * Function just check where exactly stock's view was tapped
    * It need for correct actions: add stock or delete it
     */
    private fun checkItem(item: CompanyProfile, isFavoriteList: Boolean){
        Log.d(
            TAG,
            "checkItem: item.isFavorite: ${item.isFavorite}, isFavoriteList: $isFavoriteList"
        )
        if(item.isFavorite && isFavoriteList) deleteFavorite(item)
        else if(!item.isFavorite && !isFavoriteList) addFavorite(item)
        else if(item.isFavorite && !isFavoriteList) deleteFavorite(item)
    }

    private fun addFavorite(companyProfile: CompanyProfile): Boolean {
        Log.d(TAG, "addFavorite: $companyProfile")
        setStar(R.drawable.ic_star)
        companyProfile.isFavorite = true
        return sharedPreferences.addFavorite(companyProfile.ticker)
    }
    private fun deleteFavorite(company: CompanyProfile){
        Log.d(TAG, "deleteFavorite: $company")
        setStar(R.drawable.ic_empty_star)
        company.isFavorite = false
        sharedPreferences.deleteFavorite(company.ticker)
    }
}