package ovh.geoffrey_druelle.nantestoilettes.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.Point
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking
import ovh.geoffrey_druelle.nantestoilettes.NantesToilettesApp.Companion.instance
import ovh.geoffrey_druelle.nantestoilettes.core.BaseViewModel
import ovh.geoffrey_druelle.nantestoilettes.data.local.model.Toilet
import ovh.geoffrey_druelle.nantestoilettes.data.repository.ToiletRepository
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

class MapViewModel : BaseViewModel(), CoroutineScope {

    private val job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private var repo: ToiletRepository = ToiletRepository(instance)

    private lateinit var subscription: Disposable

    private var _toiletsList = MutableLiveData<List<Toilet>>()
    val toiletsList: LiveData<List<Toilet>>
        get() = _toiletsList

    private lateinit var _geoGSONList: FeatureCollection
    val geoGSONList: FeatureCollection
        get() = _geoGSONList

    val arraySize = runBlocking { repo.countEntries() }
    val features = arrayOfNulls<Feature>(arraySize)

    init {
        getToiletsGeoCoords()
    }

    internal fun getToiletsGeoCoords() {
        lateinit var list: List<Toilet>

        subscription = repo.getToiletsList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _toiletsList.postValue(it)
                list = it
                for (i in list.indices){
                    val lat = list[i].latitude
                    val long = list[i].longitude
                    val name = list[i].name
                    val feature: Feature = Feature.fromGeometry(Point.fromLngLat(long,lat))
                    feature.addStringProperty("name",name)
                    features[i] = feature
                }
                _geoGSONList = FeatureCollection.fromFeatures(features)
            }, {
                Timber.e(it as Exception)
            })
    }

    override fun onCleared() {
        job.cancel()
        super.onCleared()
    }
}
