package ovh.geoffrey_druelle.nantestoilettes.data.remote.model

import com.google.gson.annotations.SerializedName

data class Fields(
    @SerializedName("adresse") val address: String,
    @SerializedName("commune") val city: String,
    @SerializedName("id") val id: String,
    @SerializedName("infos_horaires") val schedule: String,
    @SerializedName("lat_wgs84") val latitude: Double,
    @SerializedName("location") val location: List<Double>,
    @SerializedName("long_wgs84") val longitude: Double,
    @SerializedName("nom") val name: String,
    @SerializedName("type") val type: String,
    @SerializedName("acces_pmr") val reduceMobility: String,
    @SerializedName("pole") val pole: String
)