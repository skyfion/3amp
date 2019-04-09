package xyz.lazysoft.a3amp.view

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class AmpPageAdapter(fm: FragmentManager, private val tabs: List<String>) : FragmentPagerAdapter(fm) {

    private var fragments: List<Fragment> = AbstractThrFragment.listFragments()

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return tabs[position]
    }
}