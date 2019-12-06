package ovh.geoffrey_druelle.nantestoilettes.data.remote.api

import ovh.geoffrey_druelle.nantestoilettes.data.remote.model.General
import ovh.geoffrey_druelle.nantestoilettes.utils.DATASET
import retrofit2.Call
import retrofit2.http.GET

interface OpenDataNantesApi {

    @GET("?dataset=$DATASET&rows=0")
    fun getNHits(): Call<General>

    @GET("?dataset=$DATASET&rows=999")
    fun getFullDatas(): Call<General>
}
