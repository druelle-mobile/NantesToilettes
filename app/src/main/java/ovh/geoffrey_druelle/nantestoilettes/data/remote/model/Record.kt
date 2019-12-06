package ovh.geoffrey_druelle.nantestoilettes.data.remote.model

import com.google.gson.annotations.SerializedName

data class Record(
    @SerializedName("datasetid") val datasetid: String,
    @SerializedName("fields") val fields: Fields,
    @SerializedName("geometry") val geometry: Geometry,
    @SerializedName("record_timestamp") val record_timestamp: String,
    @SerializedName("recordid") val recordid: String
)