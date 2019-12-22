package ovh.geoffrey_druelle.nantestoilettes.ui

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mapbox.mapboxsdk.Mapbox
import org.jetbrains.anko.toast
import ovh.geoffrey_druelle.nantestoilettes.BuildConfig
import ovh.geoffrey_druelle.nantestoilettes.R
import ovh.geoffrey_druelle.nantestoilettes.ui.about.AboutFragment
import ovh.geoffrey_druelle.nantestoilettes.ui.favorites.FavoritesFragment
import ovh.geoffrey_druelle.nantestoilettes.ui.map.MapFragment
import ovh.geoffrey_druelle.nantestoilettes.ui.toiletslist.ToiletsListFragment
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber


private val TAG = MainActivity::class.java.name

class MainActivity : AppCompatActivity(),
    EasyPermissions.PermissionCallbacks,
    EasyPermissions.RationaleCallbacks {

    private val location = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    private lateinit var activeFragment: Fragment

    private val requestCodeLocationPerm = 111
    private lateinit var bottomNavigationView: BottomNavigationView

    companion object {
        lateinit var instance: MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        instance = this

        Mapbox.getInstance(this, BuildConfig.MAPBOX_API_KEY)

        activeFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)!!
        bottomNavigationView = findViewById(R.id.bottom_nav)

        initBottomNavigation()
        hasLocationPermission()
    }

    /*
    Management of bottom navigation
     */
    private fun initBottomNavigation() {
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.map -> navigateToMap()
                R.id.list -> navigateToList()
                R.id.fav -> navigateToFavorites()
                R.id.about -> navigateToAbout()
            }
            return@setOnNavigationItemSelectedListener true
        }
    }

    internal lateinit var fragment: Fragment
    fun setFragment(fragment: Fragment) {
        this.fragment = fragment
    }

    private fun navigateToMap() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)

        when (fragment) {
            is MapFragment -> return
            is ToiletsListFragment -> navHostFragment?.findNavController()
                ?.navigate(R.id.action_toiletsListFragment_to_mapFragment)
            is FavoritesFragment -> navHostFragment?.findNavController()
                ?.navigate(R.id.action_favoritesFragment_to_mapFragment)
            is AboutFragment -> navHostFragment?.findNavController()
                ?.navigate(R.id.action_aboutFragment_to_mapFragment)
        }
    }

    private fun navigateToList() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)

        when (fragment) {
            is MapFragment -> navHostFragment?.findNavController()
                ?.navigate(R.id.action_mapFragment_to_toiletsListFragment)
            is ToiletsListFragment -> return
            is FavoritesFragment -> navHostFragment?.findNavController()
                ?.navigate(R.id.action_favoritesFragment_to_toiletsListFragment)
            is AboutFragment -> navHostFragment?.findNavController()
                ?.navigate(R.id.action_aboutFragment_to_toiletsListFragment)
        }
    }

    private fun navigateToFavorites() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)

        when (fragment) {
            is MapFragment -> navHostFragment?.findNavController()
                ?.navigate(R.id.action_mapFragment_to_favoritesFragment)
            is ToiletsListFragment -> navHostFragment?.findNavController()
                ?.navigate(R.id.action_toiletsListFragment_to_favoritesFragment)
            is FavoritesFragment -> return
            is AboutFragment -> navHostFragment?.findNavController()
                ?.navigate(R.id.action_aboutFragment_to_favoritesFragment)
        }
    }

    private fun navigateToAbout() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)

        when (fragment) {
            is MapFragment -> navHostFragment?.findNavController()
                ?.navigate(R.id.action_mapFragment_to_aboutFragment)
            is ToiletsListFragment -> navHostFragment?.findNavController()
                ?.navigate(R.id.action_toiletsListFragment_to_aboutFragment)
            is FavoritesFragment -> navHostFragment?.findNavController()
                ?.navigate(R.id.action_favoritesFragment_to_aboutFragment)
            is AboutFragment -> return
        }
    }

    /*
    Init EasyPermissions checkin
     */
    private fun hasLocationPermission(): Boolean {
        return if (!EasyPermissions.hasPermissions(instance, *location)) {
            EasyPermissions.requestPermissions(
                instance,
                getString(R.string.rationale_location),
                requestCodeLocationPerm,
                *location
            )
            false
        } else
            true
    }

    /*
    Permissions management
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, instance)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        Timber.d("onPermissionsDenied: %s : %s", requestCode, perms.size)
        if (EasyPermissions.somePermissionPermanentlyDenied(instance, perms))
            AppSettingsDialog.Builder(instance).build().show()
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        Timber.d("onPermissionsGranted: %s : %s", requestCode, perms.size)
    }

    override fun onRationaleDenied(requestCode: Int) {
        Timber.d("onRationaleDenied: %s", requestCode)
    }

    override fun onRationaleAccepted(requestCode: Int) {
        Timber.d("onRationaleAccepted: %s", requestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            val yes = getString(R.string.yes)
            val no = getString(R.string.no)
            toast(
                getString(
                    R.string.returned_from_settings,
                    if (hasLocationPermission())
                        yes
                    else
                        no
                )
            )
        }
    }
}
