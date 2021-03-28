package com.wainow.island.adapter.holder

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wainow.domain.entities.Stock.Companion.TAG
import com.wainow.island.R


class ErrorViewHolder(view: View, retryCallback: () -> Unit) : RecyclerView.ViewHolder(view) {
    private val button: Button = view.findViewById(R.id.error_btn)
    private val textView: TextView = view.findViewById(R.id.error_tv)
    private val pd: ProgressBar = view.findViewById(R.id.error_pd)

    init {
        button.setOnClickListener { retryCallback() }
    }

    companion object{
        fun create(parent: ViewGroup, retryCallback: () -> Unit): ErrorViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.stock_error, parent, false)
            return ErrorViewHolder(view, retryCallback)
        }
        const val ERROR_NO_FOUND = "Stocks are not found"
        const val ERROR_REQUESTS = "HttpException"
        const val ERROR_NO_INTERNET = "UnknownHostException"
        const val ERROR_TIMEOUT = "SocketTimeoutException"
        const val ANSWER_REQUESTS = "Something wrong, you seem to have exceeded the number of requests per second"
        const val ANSWER_NO_INTERNET = "There is no internet connection, please check your connection"
        const val ANSWER_EMPTY = "Stock are not found"
        const val ANSWER_UNKNOWN = "Unknown exception :/"
        const val ANSWER_TIMEOUT = "Timeout exceeded: bad connection"
    }

    fun bindTo(isError: Boolean, name: String = ""){
        Log.d(TAG, "bindTo: isError: $isError, name: $name")
        if(!isError || name == ""){
            button.visibility = View.GONE
            textView.visibility = View.GONE
            pd.visibility = View.VISIBLE
        } else{
            button.visibility = View.VISIBLE
            textView.visibility = View.VISIBLE
            pd.visibility = View.GONE
        }
        checkException(name)
    }

    @SuppressLint("SetTextI18n")
    private fun checkException(name: String){
        when(name){
            ERROR_NO_INTERNET -> {
                textView.text = ANSWER_NO_INTERNET
            }
            ERROR_NO_FOUND -> {
                button.visibility = View.GONE
                textView.text = ANSWER_EMPTY
            }
            ERROR_REQUESTS -> textView.text = ANSWER_REQUESTS
            ERROR_TIMEOUT -> textView.text = ANSWER_TIMEOUT
            else -> { textView.text = ANSWER_UNKNOWN }
        }
    }
}