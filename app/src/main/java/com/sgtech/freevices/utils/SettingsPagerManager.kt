package com.sgtech.freevices.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.sgtech.freevices.views.ui.settings.app.AppSettingsFragment
import com.sgtech.freevices.views.ui.settings.history.HistorySettingsFragment
import com.sgtech.freevices.views.ui.settings.user.SettingsFragment

class SettingsPagerManager(activity: FragmentActivity) : FragmentStateAdapter(activity){

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> SettingsFragment()
            1 -> AppSettingsFragment()
            2 -> HistorySettingsFragment()
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}
