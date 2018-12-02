package xyz.lazysoft.a3amp

import android.app.Application
import xyz.lazysoft.a3amp.amp.AmpModule
import xyz.lazysoft.a3amp.di.AppComponent
import xyz.lazysoft.a3amp.di.AppModule
import xyz.lazysoft.a3amp.di.DaggerAppComponent
import xyz.lazysoft.a3amp.persistence.RoomModule

class AmpApplication : Application() {

//    @Inject
//    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>
//
//    override fun activityInjector(): AndroidInjector<Activity> {
//       return dispatchingAndroidInjector
//    }

    lateinit var component: AppComponent

    override fun onCreate() {
        super.onCreate()
        component = DaggerAppComponent
                .builder()
                .appModule(AppModule(this))
                .roomModule(RoomModule(this))
                .ampModule(AmpModule(this))
                .build()
        component.inject(this)
    }
}