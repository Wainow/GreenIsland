package com.wainow.island.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.wainow.island.R
import com.wainow.island.ui.list.common.CommonStockListFragment
import com.wainow.island.ui.list.favorite.FavoriteStockListFragment

private val TAB_TITLES = arrayOf(
        R.string.tab_text_1,
        R.string.tab_text_2
)

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(private val context: Context, fm: FragmentManager,
                           var listFragment: ArrayList<Fragment> = ArrayList())
    : FragmentPagerAdapter(fm) {

    init {
        listFragment = arrayListOf(
            CommonStockListFragment.newInstance(),
            FavoriteStockListFragment.newInstance()
        )
    }

    override fun getItem(position: Int): Fragment {
        return listFragment[position]
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position])
    }

    override fun getCount(): Int {
        // Show 2 total pages.
        return 2
    }
}