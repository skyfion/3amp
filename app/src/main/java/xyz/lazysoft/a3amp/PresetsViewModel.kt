package xyz.lazysoft.a3amp

import androidx.lifecycle.ViewModel
import xyz.lazysoft.a3amp.amp.Amp
import xyz.lazysoft.a3amp.persistence.AmpPreset
import xyz.lazysoft.a3amp.persistence.AmpPresetGroup
import xyz.lazysoft.a3amp.persistence.AppDatabase
import javax.inject.Inject

class PresetsViewModel : ViewModel() {

    @Inject
    lateinit var amp: Amp

    @Inject
    lateinit var repository: AppDatabase


    var selected: Any? = null

    var selectPreset: AmpPreset?
        get() {
            return amp.selectPreset
        }
        set(value) {
            amp.selectPreset = value
        }

    val groups: List<AmpPresetGroup>
        get() {
            return repository.presetDao().getAllGroups()
        }

    val presets: List<AmpPreset>
        get() {
            return repository.presetDao().getAll()
        }

    fun updatePreset(preset: AmpPreset) {
        repository.presetDao().update(preset)
    }

    fun updateGroup(group: AmpPresetGroup) {
        repository.presetDao().updateGroup(group)
    }

    fun deletePreset(preset: AmpPreset) {
        repository.presetDao().delete(preset)
    }

    fun deleteGroup(group: AmpPresetGroup) {
        repository.presetDao().deleteGroup(group)
    }

    fun insertGroup(group: AmpPresetGroup): Long {
        return repository.presetDao().insertGroup(group)
    }

    fun insertPreset(preset: AmpPreset): Long {
        return repository.presetDao().insert(preset)
    }
}
