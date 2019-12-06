package ovh.geoffrey_druelle.nantestoilettes.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import ovh.geoffrey_druelle.nantestoilettes.core.BaseDao
import ovh.geoffrey_druelle.nantestoilettes.data.local.model.Toilet

@Dao
interface ToiletDao : BaseDao<Toilet> {

    @Query("SELECT * FROM Toilet")
    fun getToiletsList() : LiveData<List<Toilet>>

    @Query("SELECT * FROM Toilet WHERE id = :id")
    fun getToiletById(id: Int) : Toilet

    @Query("SELECT COUNT(*) FROM Toilet")
    fun countEntries(): Int
}