package xyz.lazysoft.a3amp.view

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class AmpPageAdapter(fm: FragmentManager, val tabs: List<String>) : FragmentStatePagerAdapter(fm) {

    private val fragments = listOf(
            AmpFragment(),
            CompressorFragment(),
            EffectsFragment(),
            DelayFragment(),
            ReverbFragment(),
            GateFragment()
    )

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