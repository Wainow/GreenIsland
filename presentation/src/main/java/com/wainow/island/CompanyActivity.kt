package com.wainow.island

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.wainow.domain.entities.Stock
import com.wainow.island.ui.item.ItemViewModel


class CompanyActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener  {
    private lateinit var title: TextView
    private lateinit var backView: ImageView
    private lateinit var symbol: String
    private var value: Double = 0.0
    private val viewModel by lazy {
        ViewModelProvider(this).get(ItemViewModel::class.java)
    }

    companion object{
        const val NAME_SYMBOL = "symbol"
        const val NAME_VALUE = "value"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_company)
        initView()
        viewModel.liveValue.value = value
        viewModel.liveSymbol.value = symbol
        title.text = symbol
        backView.setOnClickListener {
            finish()
        }
    }

    private fun initView(){
        title = findViewById(R.id.title)
        symbol = intent.getStringExtra(NAME_SYMBOL).toString()
        value = intent.getDoubleExtra(NAME_VALUE, 0.0)
        backView = findViewById(R.id.ic_back)
    }

    override fun onRefresh() {
        Log.d(Stock.TAG, "CompanyActivity: onRefresh")
        viewModel.setData()
    }
}