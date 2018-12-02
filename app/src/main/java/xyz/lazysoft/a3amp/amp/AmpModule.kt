package xyz.lazysoft.a3amp.amp

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import xyz.lazysoft.a3amp.midi.AmpMidiManager
import javax.inject.Singleton

@Module
class AmpModule(application: Context) {

    private val midiManager = AmpMidiManager(application)
    val amp = Amp(midiManager)

    init {
        midiManager.open()
    }

    @Singleton
    @Provides
    fun providesAmp(): Amp {
        return amp
    }
}