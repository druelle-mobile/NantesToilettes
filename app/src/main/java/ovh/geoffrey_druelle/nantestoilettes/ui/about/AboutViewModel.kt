package ovh.geoffrey_druelle.nantestoilettes.ui.about

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ovh.geoffrey_druelle.nantestoilettes.NantesToilettesApp.Companion.appContext
import ovh.geoffrey_druelle.nantestoilettes.core.BaseViewModel
import ovh.geoffrey_druelle.nantestoilettes.utils.*
import ovh.geoffrey_druelle.nantestoilettes.utils.ConnectivityHelper.isConnectedToNetwork

class AboutViewModel : BaseViewModel() {
    var dbName : String = DB_NAME
    var dbOwner : String = DB_OWNER
    var dbUpdate : String = DB_UPDATE
    var dbTreatment : String = DB_TREATMENT
    var dbLicence : String = DB_LICENCE
    var author : String = AUTHOR

    private val _buttonClicked = MutableLiveData<Boolean>()
    val buttonClicked: LiveData<Boolean>
        get() = _buttonClicked

    private val _connected = MutableLiveData<Boolean>()
    val connected: LiveData<Boolean>
        get() = _connected

    init {
        testConnection()
    }

    private fun testConnection() {
        if (isConnectedToNetwork(appContext))
            _connected.postValue(true)
        else _connected.postValue(false)
    }

    fun navToWebsite(){ _buttonClicked.postValue(true) }
    fun hasNavigatedToWebsite(){ _buttonClicked.postValue(false) }
}
