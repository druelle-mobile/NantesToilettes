package ovh.geoffrey_druelle.nantestoilettes.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking
import ovh.geoffrey_druelle.nantestoilettes.NantesToilettesApp
import ovh.geoffrey_druelle.nantestoilettes.core.BaseViewModel
import ovh.geoffrey_druelle.nantestoilettes.data.local.model.Toilet
import ovh.geoffrey_druelle.nantestoilettes.data.repository.ToiletRepository
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

class MapViewModel : BaseViewModel(), CoroutineScope {

    private val job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private var repo: ToiletRepository = ToiletRepository(NantesToilettesApp.instance)

    private lateinit var subscription: Disposable

    private var _toiletsList = MutableLiveData<List<Toilet>>()
    val toiletsList: LiveData<List<Toilet>>
        get() = _toiletsList

    init {
        getToiletsGeoCoords()
    }

    private fun getToiletsGeoCoords() {
        subscription = repo.getToiletsList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _toiletsList.postValue(it)
            }, {
                Timber.e(it as Exception)
            })
    }

    override fun onCleared() {
        job.cancel()
        super.onCleared()
    }
}
