package ovh.geoffrey_druelle.nantestoilettes.ui.splashscreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking
import ovh.geoffrey_druelle.nantestoilettes.NantesToilettesApp.Companion.appContext
import ovh.geoffrey_druelle.nantestoilettes.NantesToilettesApp.Companion.instance
import ovh.geoffrey_druelle.nantestoilettes.core.BaseViewModel
import ovh.geoffrey_druelle.nantestoilettes.data.local.model.Toilet
import ovh.geoffrey_druelle.nantestoilettes.data.remote.api.OpenDataNantesApi
import ovh.geoffrey_druelle.nantestoilettes.data.remote.model.Fields
import ovh.geoffrey_druelle.nantestoilettes.data.remote.model.General
import ovh.geoffrey_druelle.nantestoilettes.data.remote.model.Record
import ovh.geoffrey_druelle.nantestoilettes.data.repository.ToiletRepository
import ovh.geoffrey_druelle.nantestoilettes.utils.ConnectivityHelper.isConnectedToNetwork
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

class SplashScreenViewModel(
    private val api: OpenDataNantesApi
) : BaseViewModel(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private var job: Job = Job()

    private val _isFinishedAnim = MutableLiveData<Boolean>()
    val isFinishedAnim: LiveData<Boolean>
        get() = _isFinishedAnim

    private val _navToMap = MutableLiveData<Boolean>()
    val navToMap: LiveData<Boolean>
        get() = _navToMap

    private val _isConnection = MutableLiveData<Boolean>()
    val isConnection: LiveData<Boolean>
        get() = _isConnection

    private val _failedRequestForDatas = MutableLiveData<Boolean>()
    val failedRequestForDatas: LiveData<Boolean>
        get() = _failedRequestForDatas

    private val _failedRequestForHits = MutableLiveData<Boolean>()
    val failedRequestForHits: LiveData<Boolean>
        get() = _failedRequestForHits
    private var repo: ToiletRepository = ToiletRepository(instance)

    var version: String = instance.getVersionNumber()

    init {
        testConnection()
    }

    override fun onCleared() {
        job.cancel()
        super.onCleared()
    }

    private fun testConnection() {
        if (isConnectedToNetwork(appContext)){
            connected()
            isLocalDatas()
        }
        else notConnected()
    }

    internal fun isLocalDatas() {
        val count = runBlocking {
            repo.countEntries()
        }

        if (count != 0)
            isSameEntriesThanRemote(count)
        else
            launchRequestForDatas()
    }

    private fun isSameEntriesThanRemote(localEntries: Int) {
        val call : Call<General> = api.getNHits()
        call.enqueue(object : Callback<General>{
            override fun onFailure(call: Call<General>, t: Throwable) {
                failedRequestForHits()
                Timber.d(String.format("isSameEntriesThanRemote: %s",t))
            }

            override fun onResponse(call: Call<General>, response: Response<General>) {
                if (response.isSuccessful){
                    succeedRequestForHits()
                    if (response.body()?.hits != localEntries) launchRequestForDatas()
                    else navToMap()
                } else{
                    failedRequestForHits()
                    Timber.d(String.format("isSameEntriesThanRemote: got response but not successful"))
                }
            }
        })
    }

    internal fun launchRequestForDatas() {
        val call : Call<General> = api.getFullDatas()
        call.enqueue(object : Callback<General> {
            override fun onFailure(call: Call<General>, t: Throwable) {
                failedRequestForDatas()
                Timber.d(String.format("launchRequestForDatas: %s",t))
            }

            override fun onResponse(call: Call<General>, response: Response<General>) {
                if (response.isSuccessful){
                    succeedRequestForDatas()
                    val general: General = response.body()!!
                    val records: List<Record> = general.records
                    for (i in records.indices){
                        val fields: Fields = records[i].fields

                        val toilet = Toilet()
                        setValuesOfToilet(toilet, fields)
                    }
                    navToMap()
                } else{
                    failedRequestForDatas()
                    Timber.d(String.format("launchRequestForDatas: got response but not successful"))
                }
            }
        })
    }

    private fun setValuesOfToilet(toilet: Toilet, fields: Fields) {
        toilet.strId = fields.id
        toilet.name = fields.name
        toilet.pole = fields.pole
        toilet.type = fields.type
        toilet.schedule = fields.schedule
        toilet.address = fields.address
        toilet.city = fields.city
        toilet.latitude = fields.latitude
        toilet.longitude = fields.longitude
        toilet.reduceMobility = fields.reduceMobility

        runBlocking {
            repo.insert(toilet)
        }
    }

    internal fun finishAnimation() {
        _isFinishedAnim.postValue(true)
    }

    internal fun alreadyFinishedAnimation() {
        _isFinishedAnim.postValue(false)
    }

    internal fun navToMap() {
        _navToMap.postValue(true)
    }

    internal fun navigatedToMap() {
        _navToMap.postValue(false)
    }

    internal fun notConnected() {
        _isConnection.postValue(false)
    }

    internal fun connected() {
        _isConnection.postValue(true)
    }

    internal fun failedRequestForHits() {
        _failedRequestForHits.postValue(true)
    }

    internal fun succeedRequestForHits() {
        _failedRequestForHits.postValue(false)
    }

    internal fun failedRequestForDatas() {
        _failedRequestForDatas.postValue(true)
    }

    internal fun succeedRequestForDatas() {
        _failedRequestForDatas.postValue(false)
    }

}
