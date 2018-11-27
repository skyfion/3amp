package xyz.lazysoft.a3amp.base

import android.app.Activity
import android.app.Application
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import xyz.lazysoft.a3amp.di.components.DaggerAppComponent
import javax.inject.Inject

class App : Application(), HasActivityInjector {

    @Inject lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    override
    fun onCreate() {
        super.onCreate()

        DaggerAppComponent
                .builder()
                .context(this)
                .build()
                .inject(this)
    }

    override fun activityInjector(): AndroidInjector<Activity> {
       return dispatchingAndroidInjector
    }

}