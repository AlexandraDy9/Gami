package com.university.gami_android.ui.events

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class ViewPagerAdapter(val type: String, fm: FragmentManager, private var tabNames: List<String>) :
    FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> {
                val eventListFragment = EventListFragment()
                eventListFragment.type = type
                return eventListFragment
            }
        }
        val eventMapFragment = EventMapFragment()
        eventMapFragment.type = type
        return eventMapFragment
    }

    override fun getCount(): Int {
        return tabNames.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return tabNames.getOrNull(position)
    }
}