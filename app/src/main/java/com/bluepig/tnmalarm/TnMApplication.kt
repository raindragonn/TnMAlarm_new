package com.bluepig.tnmalarm

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.bluepig.tnmalarm.room.AlarmDAO
import com.bluepig.tnmalarm.room.AppDataBase
import com.bluepig.tnmalarm.utils.ext.logD
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.jakewharton.threetenabp.AndroidThreeTen
import java.util.*

class TnMApplication : Application() {
    private val alarmDao: AlarmDAO by lazy { AppDataBase.getDatabase(this).alarmDao() }

    fun getDao(): AlarmDAO = alarmDao

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)

        MobileAds.initialize(this)
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)

        if (Config.DEBUG) {
            registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
                override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                    logD("${activity::class.java.simpleName} onActivityCreated")
                }

                override fun onActivityStarted(activity: Activity) {
                    logD("${activity::class.java.simpleName} onActivityStarted")
                }

                override fun onActivityResumed(activity: Activity) {
                    logD("${activity::class.java.simpleName} onActivityResumed")
                }

                override fun onActivityPaused(activity: Activity) {
                    logD("${activity::class.java.simpleName} onActivityPaused")
                }

                override fun onActivityStopped(activity: Activity) {
                    logD("${activity::class.java.simpleName} onActivityStopped")
                }

                override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
                    logD("${activity::class.java.simpleName} onActivitySaveInstanceState")
                }

                override fun onActivityDestroyed(activity: Activity) {
                    logD("${activity::class.java.simpleName} onActivityDestroyed")
                }
            })

        }
    }
}