package com.shabelnikd.noteapp.adapters

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.shabelnikd.noteapp.ui.fragments.onboard.OnBoardPageFragment

class OnBoardAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int) = OnBoardPageFragment().apply {
        arguments = bundleOf(OnBoardPageFragment.ARG_ON_BOARD_POSITION to position)
    }

}