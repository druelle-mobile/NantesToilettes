package ovh.geoffrey_druelle.nantestoilettes.ui.map

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.LayoutRes
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Observer
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.annotation.Symbol
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions
import com.mapbox.mapboxsdk.plugins.markerview.MarkerViewManager
import com.mapbox.mapboxsdk.utils.BitmapUtils
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.support.v4.toast
import org.koin.androidx.viewmodel.ext.android.getViewModel
import ovh.geoffrey_druelle.nantestoilettes.R
import ovh.geoffrey_druelle.nantestoilettes.core.BaseFragment
import ovh.geoffrey_druelle.nantestoilettes.databinding.MapFragmentBinding
import ovh.geoffrey_druelle.nantestoilettes.ui.MainActivity.Companion.instance

class MapFragment : BaseFragment<MapFragmentBinding>(), OnMapReadyCallback {

    companion object {
        fun newInstance() = MapFragment()
    }

    private var exit: Boolean = false
    private lateinit var viewModel: MapViewModel
    private lateinit var mapView: MapView
    private lateinit var mapboxMap: MapboxMap
    private lateinit var markerViewManager: MarkerViewManager
    private lateinit var symbolManager: SymbolManager

    private lateinit var drawable: Drawable
    private lateinit var bitmap: Bitmap

    @LayoutRes
    override fun getLayoutResId(): Int = R.layout.map_fragment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        super.onCreateView(inflater, container, savedInstanceState)

        instance.setFragment(this)
        instance.supportActionBar?.show()
        instance.bottom_nav.visibility = View.VISIBLE

        viewModel = getViewModel()
        binding.vm = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val root = binding.root

        drawable = ResourcesCompat.getDrawable(resources, R.drawable.ic_location_on_red_600_24dp, null)!!
        bitmap = BitmapUtils.getBitmapFromDrawable(drawable)!!

        initObservers()
        initMapView(root, savedInstanceState)

        val onBackPressedCallback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    quitApp()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        return binding.root
    }

    private fun quitApp() {
        if (exit) instance.finish()
        else {
            toast("Press Back again to exit.")
            exit = true
            Handler().postDelayed({ exit = false }, 3000)
        }
    }

    private fun initObservers() {
        viewModel.toiletsList.observe(this, Observer { })
    }

    private fun initMapView(
        root: View,
        savedInstanceState: Bundle?
    ) {
        mapView = root.findViewById(R.id.map_view)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        this@MapFragment.mapboxMap = mapboxMap
        mapboxMap.setStyle(Style.MAPBOX_STREETS) { style ->
            bitmap.let { style.addImage("MARK", it) }
            addMarkers(style)
            enableLocationComponent(style)
        }
    }

    private fun addMarkers(style: Style) {
        val list = viewModel.toiletsList.value

        symbolManager = SymbolManager(mapView, mapboxMap, style)
        symbolManager.iconAllowOverlap = true
        symbolManager.textAllowOverlap = false

        if (list != null) {
            for (i in list.indices) {
                symbolManager.create(SymbolOptions()
                    .withLatLng(LatLng(list[i].latitude, list[i].longitude))
                    .withIconImage("MARK"))
            }
        }

        symbolManager.addClickListener { symbol -> toast("clicked  " + symbol.textField.toLowerCase()) };
    }


    private fun enableLocationComponent(style: Style) {
        val locationComponent = mapboxMap.locationComponent
        locationComponent.activateLocationComponent(
            LocationComponentActivationOptions.builder(instance, style).build()
        )
        locationComponent.isLocationComponentEnabled = true
        locationComponent.cameraMode = CameraMode.NONE
        locationComponent.renderMode = RenderMode.COMPASS
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        markerViewManager.onDestroy()
        mapView.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }
}
