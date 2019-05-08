package xyz.lazysoft.a3amp.view.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import xyz.lazysoft.a3amp.AmpApplication
import xyz.lazysoft.a3amp.amp.Amp
import xyz.lazysoft.a3amp.amp.Constants
import xyz.lazysoft.a3amp.amp.Utils
import javax.inject.Inject

class AmpPreviewViewModel(val app: Application): AndroidViewModel(app) {

    @Inject
    lateinit var thr: Amp

    init {
        (app as AmpApplication).component.inject(this)
    }


    fun gain(): LiveData<Int> {
        return Transformations.map(thr.dump) {
            Utils.paramToInt(it.getValueById(Constants.K_GAIN)!!)
        }
    }

}