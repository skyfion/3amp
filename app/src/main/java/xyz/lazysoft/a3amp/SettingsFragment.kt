package xyz.lazysoft.a3amp

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import xyz.lazysoft.a3amp.persistence.AppDatabase
import javax.inject.Inject


class SettingsFragment : Fragment() {

    @Inject
    lateinit var repository: AppDatabase

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onAttach(context: Context) {
        (context.applicationContext as AmpApplication).component.inject(this)
        super.onAttach(context)
    }
}
