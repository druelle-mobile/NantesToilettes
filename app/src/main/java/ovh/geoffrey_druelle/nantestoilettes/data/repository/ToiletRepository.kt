package ovh.geoffrey_druelle.nantestoilettes.data.repository

import android.app.Application
import kotlinx.coroutines.*
import ovh.geoffrey_druelle.nantestoilettes.data.local.dao.ToiletDao
import ovh.geoffrey_druelle.nantestoilettes.data.local.database.AppDatabase
import ovh.geoffrey_druelle.nantestoilettes.data.local.model.Toilet
import kotlin.coroutines.CoroutineContext

class ToiletRepository(app: Application) : CoroutineScope{

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    private var toiletDao: ToiletDao

    init {
        val db = AppDatabase.getInstance(app)
        toiletDao = db.toiletDao()
    }

    suspend fun countEntries(): Int {
        return withContext(Dispatchers.IO){
            toiletDao.countEntries()
        }
    }

    suspend fun insert(toilet: Toilet) {
        withContext(Dispatchers.IO){
            toiletDao.insert(toilet)
        }
    }
}