package ovh.geoffrey_druelle.nantestoilettes

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.facebook.stetho.Stetho
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import ovh.geoffrey_druelle.nantestoilettes.injection.getModules
import timber.log.Timber
import timber.log.Timber.DebugTree

class NantesToilettesApp : Application() {

    companion object {
        lateinit var appContext: Context
        lateinit var instance: NantesToilettesApp
    }

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        instance = this

        if (!isRoboUnitTest() || BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
            Stetho.initializeWithDefaults(
                appContext
            )
        }

        startKoin {
            androidContext(this@NantesToilettesApp)
            modules(getModules())
        }
    }

    private fun isRoboUnitTest(): Boolean {
        return "robolectric" == Build.FINGERPRINT
    }

    fun getVersionNumber(): String {
        try {
            val pInfo = this.packageManager.getPackageInfo(packageName, 0)
            return pInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        return "0.0.0"
    }
}