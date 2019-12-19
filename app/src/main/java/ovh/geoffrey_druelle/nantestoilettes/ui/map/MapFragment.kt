package ovh.geoffrey_druelle.nantestoilettes.ui.map

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PointF
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.annotation.LayoutRes
import androidx.core.content.res.ResourcesCompat.getDrawable
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.annotations.BubbleLayout
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions.*
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.CameraMode.*
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.location.modes.RenderMode.*
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.MapboxMap.OnMapClickListener
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.expressions.Expression.*
import com.mapbox.mapboxsdk.style.layers.Property.ICON_ANCHOR_BOTTOM
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.*
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.mapbox.mapboxsdk.utils.BitmapUtils.getBitmapFromDrawable
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import org.jetbrains.anko.support.v4.toast
import org.koin.androidx.viewmodel.ext.android.getViewModel
import ovh.geoffrey_druelle.nantestoilettes.R
import ovh.geoffrey_druelle.nantestoilettes.core.BaseFragment
import ovh.geoffrey_druelle.nantestoilettes.databinding.MapFragmentBinding
import ovh.geoffrey_druelle.nantestoilettes.ui.MainActivity.Companion.instance
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import kotlin.coroutines.CoroutineContext

class MapFragment : BaseFragment<MapFragmentBinding>(), OnMapReadyCallback, OnMapClickListener,
    CoroutineScope {

    private val job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    companion object {
        fun newInstance() = MapFragment()

    }

    private var exit: Boolean = false
    private lateinit var viewModel: MapViewModel
    private lateinit var mapView: MapView
    private lateinit var mapboxMap: MapboxMap
    private lateinit var featureCollection: FeatureCollection
    private lateinit var drawable: Drawable
    private lateinit var bitmap: Bitmap
    private lateinit var source: GeoJsonSource
    private var refreshSource: Boolean = false

    private val GEOJSON_SOURCE_ID = "GEOJSON_SOURCE_ID"
    private val MARKER_IMAGE_ID = "MARKER_IMAGE_ID"
    private val MARKER_LAYER_ID = "MARKER_LAYER_ID"
    private val CALLOUT_LAYER_ID = "CALLOUT_LAYER_ID"
    private val PROPERTY_SELECTED = "selected"
    private val PROPERTY_NAME = "name"

    @LayoutRes
    override fun getLayoutResId(): Int = R.layout.map_fragment

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
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

        drawable = getDrawable(resources, R.drawable.ic_location_on_red_600_24dp, null)!!
        bitmap = getBitmapFromDrawable(drawable)!!
        initMapView(root, savedInstanceState)

        val onBackPressedCallback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                quitApp()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        return root
    }

    private fun quitApp() {
        if (exit) instance.finish()
        else {
            toast("Press Back again to exit.")
            exit = true
            Handler().postDelayed({ exit = false }, 3000)
        }
    }

    private fun initMapView(root: View, savedInstanceState: Bundle?) {
        mapView = root.findViewById(R.id.map_view)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap
        mapboxMap.setStyle(Style.MAPBOX_STREETS) {
            launch {
                featureCollection = loadDatas()
                setUpData(featureCollection)
                onPostLoadDatas()
                val bitmapHash: HashMap<String, Bitmap> = generateViewIcon(featureCollection)
                onPostGenerateIcon(bitmapHash)
            }

            mapboxMap.addOnMapClickListener(this@MapFragment)
        }
    }

    private suspend fun loadDatas(): FeatureCollection {
        return withContext(Dispatchers.Default) {
            viewModel.geoGSONList
        }
    }

    private fun setUpData(collection: FeatureCollection) {
        featureCollection = collection
        mapboxMap.getStyle { style ->
            setUpUserLocation(style)
            setUpSource(style)
            setUpImage(style)
            setUpMarkerLayer(style)
            setUpInfoWindowLayer(style)
        }
    }

    private suspend fun onPostLoadDatas() {
        withContext(Dispatchers.Default) {
            val list = featureCollection.features()
            if (list != null) {
                for (i in list.indices) {
                    list[i].addBooleanProperty(PROPERTY_SELECTED, false)
                    list[i].addStringProperty(PROPERTY_NAME, viewModel.toiletsList.value!![i].name)
                }
            }
        }
    }

    private fun setUpUserLocation(style: Style) {
        val locationComponent = mapboxMap.locationComponent
        locationComponent.activateLocationComponent(
            builder(instance, style).build()
        )
        locationComponent.isLocationComponentEnabled = true
        locationComponent.cameraMode = NONE
        locationComponent.renderMode = COMPASS
    }

    private fun setUpSource(style: Style) {
        source = GeoJsonSource(GEOJSON_SOURCE_ID, featureCollection)
        style.addSource(source)
    }

    private fun setUpImage(style: Style) {
        bitmap.let { style.addImage(MARKER_IMAGE_ID, it) }
    }

    private fun setUpMarkerLayer(style: Style) {
        style.addLayer(
            SymbolLayer(MARKER_LAYER_ID, GEOJSON_SOURCE_ID)
                .withProperties(
                    iconImage(MARKER_IMAGE_ID),
                    iconAllowOverlap(true),
                    iconOffset(arrayOf(0f, -8f))
                )
        )
    }

    private fun setUpInfoWindowLayer(style: Style) {
        style.addLayer(
            SymbolLayer(CALLOUT_LAYER_ID, GEOJSON_SOURCE_ID)
                .withProperties(
                    iconImage("{name}"),
                    iconAnchor(ICON_ANCHOR_BOTTOM),
                    iconAllowOverlap(true),
                    iconOffset(arrayOf(-2f, -28f))
                )
                .withFilter(
                    eq(
                        get(PROPERTY_SELECTED),
                        literal(true)
                    )
                )
        )
    }

    private suspend fun generateViewIcon(featureCollection: FeatureCollection): HashMap<String, Bitmap> {
        val viewMap = HashMap<String, View>()
        val imagesMap = HashMap<String, Bitmap>()
        val layoutInflater = LayoutInflater.from(this.context)
        val collection: FeatureCollection = featureCollection

        return withContext(Dispatchers.Default) {
            for (feature in collection.features()!!) {
                val bubbleLayout: BubbleLayout =
                    layoutInflater.inflate(R.layout.bubble_layout, null) as BubbleLayout

                val name = feature.getStringProperty(PROPERTY_NAME)
                val titleTextView = bubbleLayout.findViewById<TextView>(R.id.info_window_title)
                titleTextView.text = name

                val measureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                bubbleLayout.measure(measureSpec, measureSpec)

                val measuredWidth = bubbleLayout.measuredWidth.toFloat()

                bubbleLayout.arrowPosition = measuredWidth / 2 - 5

                val bitmap: Bitmap = generateSymbol(bubbleLayout)
                imagesMap[name] = bitmap
                viewMap[name] = bubbleLayout
            }
            return@withContext imagesMap
        }
    }

    private fun generateSymbol(view: View): Bitmap {
        val measureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        view.measure(measureSpec, measureSpec)

        val measuredWidth = view.measuredWidth
        val measuredHeight = view.measuredHeight

        view.layout(0, 0, measuredWidth, measuredHeight)
        val bitmap = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888)
        bitmap.eraseColor(Color.TRANSPARENT)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    private fun onPostGenerateIcon(bitmapHash: HashMap<String, Bitmap>) {
        mapboxMap.getStyle { style: Style ->
            style.addImages(bitmapHash)
            if (refreshSource) refreshSource()
        }
    }

    override fun onMapClick(point: LatLng): Boolean {
        return handleClickIcon(mapboxMap.projection.toScreenLocation(point))
    }

    private fun handleClickIcon(pointF: PointF): Boolean {
        val selectedFeatures: List<Feature> = mapboxMap.queryRenderedFeatures(pointF, MARKER_LAYER_ID)
        val decimalFormat = NumberFormat.getNumberInstance(Locale("en","UK")) as DecimalFormat
        decimalFormat.applyPattern("#.###")
        val featureList = featureCollection.features()

        return if (selectedFeatures.isNotEmpty()) {
            val name = selectedFeatures[0].getStringProperty(PROPERTY_NAME)
            val selectedFeature : Point = selectedFeatures[0].geometry() as Point
            val sfLat : Double = decimalFormat.format(selectedFeature.latitude()).toDouble()
            val sfLng : Double = decimalFormat.format(selectedFeature.longitude()).toDouble()

            if (featureList != null) {
                for (i in featureList.indices) {
                    if (featureList[i].getStringProperty(PROPERTY_NAME) == name) {
                        val featurePoint : Point = featureList[i].geometry() as Point
                        val fLat : Double = decimalFormat.format(featurePoint.latitude()).toDouble()
                        val fLng : Double = decimalFormat.format(featurePoint.longitude()).toDouble()

                        if (fLat == sfLat && fLng == sfLng){
                            if (featureSelectStatus(i)) setFeatureSelectState(featureList[i], false)
                            else setSelected(i)
                        }
                    }
                }
            }
            true
        } else {
            if (featureList != null) {
                for (i in featureList.indices) {
                    if (featureList[i].getBooleanProperty(PROPERTY_SELECTED) == true) {
                        setFeatureSelectState(featureList[i], false)
                    }
                }
            }
            false
        }
    }

    private fun setSelected(index: Int) {
        if (featureCollection.features() != null) {
            val feature = featureCollection.features()!![index]
            setFeatureSelectState(feature, true)
            refreshSource()
        }
    }

    private fun setFeatureSelectState(
        feature: Feature,
        selectedState: Boolean
    ) {
        if (feature.properties() != null) {
            feature.properties()!!.addProperty(PROPERTY_SELECTED, selectedState)
            refreshSource()
        }
    }

    private fun featureSelectStatus(index: Int): Boolean {
        return featureCollection.features()!![index].getBooleanProperty(PROPERTY_SELECTED)
    }

    private fun refreshSource() {
        source.setGeoJson(featureCollection)
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
        job.cancel()
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onDestroyView() {
        job.cancel()
        super.onDestroyView()
        mapView.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }
}