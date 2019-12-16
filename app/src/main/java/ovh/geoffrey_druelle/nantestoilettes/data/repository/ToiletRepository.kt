package ovh.geoffrey_druelle.nantestoilettes.data.repository

import android.app.Application
import io.reactivex.Single
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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

    suspend fun getToiletsList(): List<Toilet>? {
        return withContext(Dispatchers.IO){
            toiletDao.getToiletsList().value
        }
    }

    fun getToiletsList2() : Single<List<Toilet>> {
        return toiletDao.getToiletsList2()
    }

    fun getFavoritesList() : Single<List<Toilet>> {
        return toiletDao.getFavoritesList()
    }

    suspend fun isToiletInFavorites(id: Int) : Boolean {
        val value = withContext(Dispatchers.IO){
            toiletDao.isToiletInFavorites(id)
        }
        return value == 1
    }

    suspend fun updateFavoriteField(id: Int, isFav: Boolean) {
        withContext(Dispatchers.IO){
            toiletDao.updateFavoriteField(id, isFav)
        }
    }

    suspend fun getToiletById(id: Int) : Toilet {
        return withContext(Dispatchers.IO){
            toiletDao.getToiletById(id)
        }
    }
}