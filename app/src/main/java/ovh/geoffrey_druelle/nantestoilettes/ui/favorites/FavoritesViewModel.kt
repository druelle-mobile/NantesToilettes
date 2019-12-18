package ovh.geoffrey_druelle.nantestoilettes.ui.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import ovh.geoffrey_druelle.nantestoilettes.NantesToilettesApp
import ovh.geoffrey_druelle.nantestoilettes.core.BaseViewModel
import ovh.geoffrey_druelle.nantestoilettes.data.local.model.Toilet
import ovh.geoffrey_druelle.nantestoilettes.data.repository.ToiletRepository
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

class FavoritesViewModel : BaseViewModel(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private var job: Job = Job()

    private var repo = ToiletRepository(NantesToilettesApp.instance)

    private lateinit var subscription: Disposable

    private val _toilets: MutableLiveData<List<Toilet>> = MutableLiveData(listOf())
    val toilets: LiveData<List<Toilet>>
        get() = _toilets

    private val _isInFavorites: MutableLiveData<Boolean> = MutableLiveData()
    val isInFavorites: LiveData<Boolean>
        get() = _isInFavorites

    private val _isInFavo: MutableLiveData<Int> = MutableLiveData()
    val isInFavo: LiveData<Int>
        get() = _isInFavo

    init {
        loadFavoritesList()
    }

    override fun onCleared() {
        job.cancel()
        super.onCleared()
    }

    private fun loadFavoritesList() {
        subscription = repo.getFavoritesList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _toilets.value = it
            }, {
                Timber.e(it as Exception)
            })
    }
}
